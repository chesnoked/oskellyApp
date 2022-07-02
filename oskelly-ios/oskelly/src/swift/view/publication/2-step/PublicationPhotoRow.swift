//
// Created by Виталий Хлудеев on 22.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit
import Dispatch
import DWImagePicker_Swift3


class PublicationPhotoRow : UITableViewCell, WDImagePickerDelegate {

    let imagePreview = UIImageView(image: UIImage(named: "assets/images/publication/DraftPlaceHolder.png"))
    let placeholderImage = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")
    let photoNumberLabel: UILabel = UILabel()
    var controller: UIViewController!
    var photoLoaded: Bool = false
    var camera: DSCameraHandler?
    var photoNumber: Int!
    var productId: Int!
    var draft: Draft!
    let exampleButton = UIButton()
    let exampleImage = UIImageView(image: UIImage(named: "assets/images/publication/DraftPlaceHolder.png"))

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addImagePreview()
        addSeparator()
        addPhotoNumberLabel()
        addExampleButton()

        DispatchQueue.global(qos: .background).async {
            DispatchQueue.main.async {
                self.initCamera()
            }
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(draft: Draft, photoNumber: Int, controller: UIViewController) {
        self.photoLoaded = false
        self.draft = draft
        photoNumberLabel.text = String(photoNumber) + "-е фото"
        self.controller = controller
        self.photoNumber = photoNumber
        self.productId = draft.id

        if let i: Int =  draft.images!.index(where: { $0.order == photoNumber }) {
            if let imageUrl = draft.images![i].url {
                let url = URL(string: imageUrl)!
                self.photoLoaded = true
                imagePreview.af_setImage(withURL: url, placeholderImage: placeholderImage)
            }
            else {
                imagePreview.image = placeholderImage
            }
        }
        else {
            imagePreview.image = placeholderImage
        }
        if let url = draft.samples?.first(where: {$0.photoOrder == self.photoNumber})?.imagePath {
            exampleButton.isHidden = false
            exampleImage.af_setImage(withURL: URL(string: ApiRequester.domain + url)!, placeholderImage: placeholderImage)
        }
        else {
            exampleButton.isHidden = true
            exampleImage.image = placeholderImage
        }
    }

    private func initCamera() {
        camera = DSCameraHandler(delegate_: self)
    }

    func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
    {
        let myActionSheet = UIAlertController(title: nil, message: nil, preferredStyle: UIAlertControllerStyle.actionSheet)

        if self.camera == nil {
            initCamera()
        }

        let optionMenu = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        optionMenu.popoverPresentationController?.sourceView = controller.view

        let cameraAction = UIAlertAction(title: "Камера", style: UIAlertActionStyle.default) { (action) in
            self.camera!.getCameraOn(self.controller, canEdit: false)
        }

        let galleryAction = UIAlertAction(title: "Галерея", style: UIAlertActionStyle.default) { (action) in
            self.camera!.getPhotoLibraryOn(self.controller, canEdit: false)
        }


        let viewPhotoAction = UIAlertAction(title: "Просмотр", style: UIAlertActionStyle.default) { (action) in
            self.fullScreenImage(imageView: self.imagePreview)
        }

        let cancelAction = UIAlertAction(title: "Отмена", style: UIAlertActionStyle.cancel) { (action) in

        }

        if(self.photoLoaded) {
            myActionSheet.addAction(viewPhotoAction)
        }
        myActionSheet.addAction(cameraAction)
        myActionSheet.addAction(galleryAction)
        myActionSheet.addAction(cancelAction)

        // support iPads (popover view)
//        myActionSheet.popoverPresentationController?.sourceView = self.showActionSheetButton
//        myActionSheet.popoverPresentationController?.sourceRect = self.showActionSheetButton.bounds

        controller.present(myActionSheet, animated: true, completion: nil)
    }


    func fullScreenImage(imageView: UIImageView) {
        let newImageView = UIImageView(image: imageView.image)
        newImageView.frame = controller.view.bounds
        newImageView.backgroundColor = .black
        newImageView.contentMode = .scaleAspectFit
        newImageView.isUserInteractionEnabled = true
        let tap = UITapGestureRecognizer(target: self, action: #selector(dismissFullscreenImage))
        newImageView.addGestureRecognizer(tap)
        controller.view.addSubview(newImageView)
//        controller.navigationController?.isNavigationBarHidden = true
        controller.navigationController?.navigationBar.isHidden = true
        controller.tabBarController?.tabBar.isHidden = true
    }

    func dismissFullscreenImage(_ sender: UITapGestureRecognizer) {
//        controller.navigationController?.isNavigationBarHidden = false
//        controller.tabBarController?.tabBar.isHidden = false

        controller.navigationController?.navigationBar.isHidden = false
        controller.tabBarController?.tabBar.isHidden = false


        sender.view?.removeFromSuperview()
    }

    func imagePicker(imagePicker: WDImagePicker, pickedImage: UIImage) {

        let image = pickedImage

        self.imagePreview.image = image
        self.photoLoaded = true
//        imagePicker.dismiss(animated: true, completion: nil)

        GlobalProvider.instance.draftProvider.uploadPhoto(image: image, productId: productId, imageOrder: photoNumber) { d in
            self.draft = GlobalProvider.instance.draftProvider.setCurrent(draft: d)
        }
    }

    func imagePickerDidCancel(imagePicker: WDImagePicker) {

    }

    private func addImagePreview() {
        contentView.addSubview(imagePreview)
        imagePreview.frame = CGRect(x: 10, y: 10, width: 75, height: 75)
        imagePreview.contentMode = UIViewContentMode.scaleToFill
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        imagePreview.isUserInteractionEnabled = true
        imagePreview.addGestureRecognizer(tapGestureRecognizer)
    }

    private func addPhotoNumberLabel() {
        contentView.addSubview(photoNumberLabel)
        photoNumberLabel.font = BoldFont.systemFont(ofSize: 14)
        photoNumberLabel.text = "Номер фото"
        photoNumberLabel.snp.makeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView)
            make.left.equalTo(imagePreview.snp.right).offset(12)
        }
    }

    private func addSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addExampleButton() {
        exampleButton.setTitle("Пример", for: .normal)
        exampleButton.layer.borderWidth = 0
        exampleButton.setTitleColor(.gray, for: .normal)
        contentView.addSubview(exampleButton)
        exampleButton.snp.makeConstraints({m in
            m.centerY.equalTo(contentView)
            m.right.equalTo(contentView).inset(12)
        })
        exampleButton.addTarget(self, action: #selector(self.exampleButtonTapped), for: .touchUpInside)
    }

    func exampleButtonTapped() {
        self.fullScreenImage(imageView: exampleImage)
    }
}