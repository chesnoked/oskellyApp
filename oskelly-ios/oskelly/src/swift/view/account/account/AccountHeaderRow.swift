//
// Created by Виталий Хлудеев on 21.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountHeaderRow : UITableViewCell, UINavigationControllerDelegate, UIImagePickerControllerDelegate {

    private let avatar = ProfilePhotoView()
    private let nickname = UILabel()
    private let email = UILabel()
    private let viewProfile = UILabel()
    private let userPhotoHeight: CGFloat = 100.0
    private let separator = UIView()
    private var controller: UIViewController?
    private var camera: DSCameraHandler2?

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        isUserInteractionEnabled = true
        selectionStyle = .none
        addAvatar()
        addNickname()
        addEmail()
        addViewProfile()
        addSeparator()

        DispatchQueue.global(qos: .background).async {
            DispatchQueue.main.async {
                self.camera = DSCameraHandler2(delegate_: self)
            }
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(account: Account, controller: UIViewController) {
        self.controller = controller
        if let url = account.avatar {
            self.avatar.af_setImage(withURL: URL(string: ApiRequester.domain + url)!)
        }
        nickname.text = account.nickname
        email.text = account.email
    }

    private func addAvatar() {
        contentView.addSubview(avatar)
        avatar.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(userPhotoHeight)
            make.height.equalTo(userPhotoHeight)
            make.top.equalTo(contentView).inset(15)
            make.left.equalTo(contentView).inset(15)
        }
        avatar.layer.cornerRadius = userPhotoHeight / 2

        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        avatar.isUserInteractionEnabled = true
        avatar.addGestureRecognizer(tapGestureRecognizer)
    }

    func imageTapped(tapGestureRecognizer: UITapGestureRecognizer) {
        if(self.camera == nil) {
            self.camera = DSCameraHandler2(delegate_: self)
        }

        let optionMenu = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        optionMenu.popoverPresentationController?.sourceView = self.controller?.view

        let cameraAction = UIAlertAction(title: "Камера", style: UIAlertActionStyle.default) { (action) in
            self.camera!.getCameraOn(self.controller, canEdit: true)
        }

        let galleryAction = UIAlertAction(title: "Галерея", style: UIAlertActionStyle.default) { (action) in
            self.camera!.getPhotoLibraryOn(self.controller, canEdit: true)
        }

        let cancelAction = UIAlertAction(title: "Отмена", style: UIAlertActionStyle.cancel) { (action) in

        }

        let myActionSheet = UIAlertController(title: nil, message: nil, preferredStyle: UIAlertControllerStyle.actionSheet)
        myActionSheet.addAction(cameraAction)
        myActionSheet.addAction(galleryAction)
        myActionSheet.addAction(cancelAction)

        // support iPads (popover view)
//        myActionSheet.popoverPresentationController?.sourceView = self.showActionSheetButton
//        myActionSheet.popoverPresentationController?.sourceRect = self.showActionSheetButton.bounds

        controller?.present(myActionSheet, animated: true, completion: nil)
    }

    private func addNickname() {
        contentView.addSubview(nickname)
        nickname.font = BoldFont.systemFont(ofSize: 15)
        nickname.snp.makeConstraints({ m in
            m.top.equalTo(avatar).inset(10)
            m.left.equalTo(avatar.snp.right).offset(15)
        })
    }

    private func addEmail() {
        contentView.addSubview(email)
        email.font = RegularFont.systemFont(ofSize: 15)
        email.textColor = .darkGray
        email.snp.makeConstraints({ m in
            m.left.equalTo(nickname)
            m.centerY.equalTo(avatar)
        })
    }

    private func addViewProfile() {
        contentView.addSubview(viewProfile)
        viewProfile.font = RegularFont.systemFont(ofSize: 15)
        viewProfile.text = "Смотреть профиль"
        let tap = UITapGestureRecognizer(target: self, action: #selector(self.viewProfileTap))
        viewProfile.isUserInteractionEnabled = true
        viewProfile.addGestureRecognizer(tap)
        viewProfile.snp.makeConstraints({ m in
            m.left.equalTo(email)
            m.bottom.equalTo(avatar).inset(5)
        })
    }

    private func addSeparator() {
        contentView.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView).inset(15)
        }
    }

    func viewProfileTap() {
        GlobalProvider.instance.profileProvider.getCurrentProfile(completionHandler: {p in
            let c = self.controller?.storyboard?.instantiateViewController(withIdentifier: "ProfileProductsController") as? ProfileProductsController
            c?.profile = p
            c.map({self.controller?.navigationController?.pushViewController($0, animated: true)})
        })
    }

    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        let image = info[UIImagePickerControllerEditedImage] as? UIImage
        let backup = self.avatar.image

        self.avatar.image = image
        picker.dismiss(animated: true, completion: nil)

        if let img = image {
            GlobalProvider.instance.getApiRequester().updateAvatar(image: img, completeHandler: {
                GlobalProvider.instance.accountProvider.synchronize()
            }, errorHandler: { e in
                self.avatar.image = backup

                let alert = UIAlertController(title: "Ошибка загрузки фото", message: e, preferredStyle: UIAlertControllerStyle.alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
                self.controller?.present(alert, animated: true, completion: nil)
            })
        }
        self.camera = DSCameraHandler2(delegate_: self) // хз, но без этого тупит
    }
}