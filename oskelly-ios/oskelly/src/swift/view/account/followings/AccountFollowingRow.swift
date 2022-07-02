//
// Created by Виталий Хлудеев on 16.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountFollowingRow : UITableViewCell {

    private let initiatorAvatar = ProfilePhotoView()
    private let initiatorNickname = UILabel()
    private let productsCount = UILabel()
    private let followingsCount = UILabel()
    private let followButton = FollowingButton()
    private let imgWidth : CGFloat = 47.0

    private var controller: AccountFollowingsController?
    private var following: Following?

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        addInitiatorAvatar()
        addInitiatorNickname()
        addProductsCount()
        addFollowingsCount()
        addFollowingButton()
    }

    private func addFollowingButton() {
        contentView.addSubview(followButton)
        followButton.snp.makeConstraints({m in
            m.right.equalTo(contentView).inset(10)
            m.centerY.equalTo(initiatorAvatar)
            m.height.equalTo(25)
            m.width.equalTo(100)
        })
        followButton.titleLabel?.font = BoldFont.systemFont(ofSize: 13)
        followButton.addTarget(self, action: #selector(self.follow), for: .touchUpInside)
    }

    func follow() {
        following?.id.map({
            GlobalProvider.instance.getApiRequester().followingToggle(profileId: $0, completionHandler: {
                GlobalProvider.instance.profileProvider.synchronizeProfile()
            })
        })
        following?.doIFollow = !(following?.doIFollow ?? false)
        followButton.render(doIFollow: following?.doIFollow)
    }

    private func addFollowingsCount() {
        contentView.addSubview(followingsCount)
        followingsCount.font = MediumFont.systemFont(ofSize: 12)
        followingsCount.text = "0 подписчиков"
        followingsCount.snp.makeConstraints({ m in
            m.left.equalTo(initiatorNickname)
            m.bottom.equalTo(initiatorAvatar)
        })
    }

    private func addProductsCount() {
        contentView.addSubview(productsCount)
        productsCount.font = MediumFont.systemFont(ofSize: 12)
        productsCount.text = "0 товаров на продажу"
        productsCount.snp.makeConstraints({ m in
            m.left.equalTo(initiatorNickname)
            m.centerY.equalTo(initiatorAvatar)
        })
    }

    private func addInitiatorNickname() {
        contentView.addSubview(initiatorNickname)
        initiatorNickname.font = BoldFont.systemFont(ofSize: 12)
        initiatorNickname.text = "Deluxe"
        initiatorNickname.snp.makeConstraints({m in
            m.top.equalTo(initiatorAvatar)
            m.left.equalTo(initiatorAvatar.snp.right).offset(10)
        })
    }

    private func addInitiatorAvatar() {
        contentView.addSubview(initiatorAvatar)
        initiatorAvatar.snp.makeConstraints({m in
            m.left.equalTo(contentView).inset(10)
            m.top.equalTo(contentView).inset(7)
            m.bottom.equalTo(contentView).inset(7)
            m.height.equalTo(imgWidth)
            m.width.equalTo(imgWidth)
        })
        initiatorAvatar.layer.cornerRadius = imgWidth / 2
        initiatorAvatar.isUserInteractionEnabled = true
        let tap = UITapGestureRecognizer(target: self, action: #selector(initiatorAvatarClick))
        initiatorAvatar.addGestureRecognizer(tap)
    }

    func initiatorAvatarClick(_ sender: UITapGestureRecognizer) {
        let c = controller?.storyboard?.instantiateViewController(withIdentifier: "ProfileProductsController") as? ProfileProductsController
        c?.profileId = following?.id
        c.map({controller?.navigationController?.pushViewController($0, animated: true)})
    }

    func render(following: Following?, controller: AccountFollowingsController) {
        self.following = following
        self.controller = controller
        initiatorAvatar.setImage(uri: following?.avatar)
        initiatorNickname.text = following?.nickname
        productsCount.text = String(following?.productsForSale ?? 0) + " товаров на продажу"
        followingsCount.text =  String(following?.subscribers ?? 0) + " подписчиков"
        followButton.render(doIFollow: following?.doIFollow)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}