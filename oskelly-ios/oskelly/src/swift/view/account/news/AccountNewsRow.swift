//
// Created by Виталий Хлудеев on 12.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountNewsRow : UITableViewCell {

    private let initiatorAvatar = ProfilePhotoView()
    private let initiatorNickname = UILabel()
    private let createTime = UILabel()
    private let baseMessage = UILabel()
    private let targetUserNickname = UILabel()
    private let imageOfTargetObject = UIImageView()
    private let imgWidth : CGFloat = 54.0
    private let stubView = UIView() // прослойка

    private let stubImg = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")
    private var notification: Notification!
    private var controller: UIViewController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        addInitiatorAvatar()
        addImageOfTargetObject()
        addStubView()
        addInitiatorNickname()
        addCreateTime()
        addBaseMessage()
        addSeparator()
    }

    private func addSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { m in
            m.height.equalTo(1)
            m.bottom.equalTo(contentView)
            m.right.equalTo(contentView)
            m.left.equalTo(contentView)
        }
    }

    private func addStubView() {
        contentView.addSubview(stubView)
        stubView.snp.makeConstraints({m in
            m.right.equalTo(imageOfTargetObject.snp.left)
            m.width.equalTo(10)
            m.height.equalTo(10)
        })
    }

    private func addImageOfTargetObject() {
        contentView.addSubview(imageOfTargetObject)
        imageOfTargetObject.contentMode = .scaleAspectFit
        imageOfTargetObject.image = stubImg
        imageOfTargetObject.snp.makeConstraints({m in
            m.top.equalTo(initiatorAvatar)
            m.bottom.equalTo(initiatorAvatar)
            m.width.equalTo(initiatorAvatar)
            m.right.equalTo(contentView).inset(10)
        })
        imageOfTargetObject.isUserInteractionEnabled = true
        let tap = UITapGestureRecognizer(target: self, action: #selector(imageOfTargetObjectClick))
        imageOfTargetObject.addGestureRecognizer(tap)
    }

    func imageOfTargetObjectClick(_ sender: UITapGestureRecognizer) {
        GlobalProvider.instance.navigator.navigateToNotificationTarget(notification: notification, controller: controller)
    }

    private func addBaseMessage() {
        contentView.addSubview(baseMessage)
        baseMessage.font = MediumFont.systemFont(ofSize: 12)
        baseMessage.numberOfLines = 0
        baseMessage.text = "подписался(-ась) на новости подписался(-ась) на новости подписался(-ась) на новости подписался(-ась) на новости подписался(-ась) на новости"
        baseMessage.snp.makeConstraints({m in
            m.left.equalTo(initiatorNickname)
            m.top.equalTo(createTime.snp.bottom).offset(5)
            m.bottom.equalTo(contentView).inset(10)
            m.right.equalTo(initiatorNickname)
        })
    }

    private func addCreateTime() {
        contentView.addSubview(createTime)
        createTime.font = MediumFont.systemFont(ofSize: 12)
        createTime.text = "(01.01.2018 12:00)"
        createTime.textColor = .gray
        createTime.snp.makeConstraints({m in
            m.left.equalTo(initiatorNickname)
            m.top.equalTo(initiatorNickname.snp.bottom).offset(1)
            m.right.equalTo(initiatorNickname)
        })
    }

    private func addInitiatorNickname() {
        contentView.addSubview(initiatorNickname)
        initiatorNickname.font = BoldFont.systemFont(ofSize: 12)
        initiatorNickname.text = "Deluxe"
        initiatorNickname.snp.makeConstraints({m in
            m.top.equalTo(initiatorAvatar)
            m.left.equalTo(initiatorAvatar.snp.right).offset(10)
            m.right.equalTo(stubView.snp.left)
        })
    }

    private func addInitiatorAvatar() {
        contentView.addSubview(initiatorAvatar)
        initiatorAvatar.snp.makeConstraints({m in
            m.left.equalTo(contentView).inset(10)
            m.top.equalTo(contentView).inset(7)
            m.height.equalTo(imgWidth)
            m.width.equalTo(imgWidth)
        })
        initiatorAvatar.layer.cornerRadius = imgWidth / 2
        initiatorAvatar.isUserInteractionEnabled = true
        let tap = UITapGestureRecognizer(target: self, action: #selector(initiatorAvatarClick))
        initiatorAvatar.addGestureRecognizer(tap)
    }

    func initiatorAvatarClick(_ sender: UITapGestureRecognizer) {
        if let id = notification.initiatorId {
            let c = self.controller.storyboard!.instantiateViewController(withIdentifier: "ProfileProductsController") as! ProfileProductsController
            c.profileId = id
            controller.navigationController?.pushViewController(c, animated: true)
        }
    }

    func render(notification: Notification, controller: UIViewController) {
        self.notification = notification
        self.controller = controller

        initiatorAvatar.setDefaultImage()
        imageOfTargetObject.image = nil

        if let im = notification.initiatorAvatar {
            let url = URL(string: ApiRequester.domain + im)!
            initiatorAvatar.af_setImage(withURL: url, placeholderImage: nil)
        }

        if let im = notification.imageOfTargetObject {
            let url = URL(string: ApiRequester.domain + im)!
            imageOfTargetObject.af_setImage(withURL: url, placeholderImage: nil)
        }

        initiatorNickname.text = notification.initiatorNickname
        baseMessage.text = (notification.baseMessage ?? "") + (notification.targetUserNickname.map({" " + $0}) ?? "")
        if let v = notification.createTime {
            createTime.text = "(" + GlobalProvider.instance.dateTimeConverter.fromDateTimeWithTimeZone(dateString: v, format: "dd.MM.yyyy HH:mm") + " )"
        }
        else {
            createTime.text = nil
        }

        baseMessage.snp.remakeConstraints({m in
            m.left.equalTo(initiatorNickname)
            m.top.equalTo(createTime.snp.bottom).offset(5)
            m.bottom.equalTo(contentView).inset(notification.initiatorNickname != nil ? 10 : 21)
            m.right.equalTo(initiatorNickname)
        })
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}