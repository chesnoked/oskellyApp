//
// Created by Виталий Хлудеев on 09.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProfileHeader : UICollectionViewCell {

    static let scrollViewHeight: CGFloat = 55

    let minWidth = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)
    let itemTitles = ["Товары", "Новости", "Wish list", "Фавориты"]
    var items: [ProfileHeaderItem] = []
    let container = UIView()
    let profilePhoto = ProfilePhotoView()
    let profileDescription = UILabel()
    let usernameLabel = UILabel()
    let likes = UILabel()
    let scrollView = UIScrollView()
    let itemWidth: CGFloat = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height) / 4
    let separator = UIView()
    let separator1 = UIView()

    let topContainer = UIView()
    let productsCount = TopItem()
    let followingsCount = TopItem()
    let followersCount = TopItem()
    let followButton = FollowingButton()
    let accountButton = DarkButton()

    let imgHeight : CGFloat = 75.0

    var controller: ProfileProductsController?
    var profileNewsController: ProfileNewsController?
    var profile: PublicProfile?


    override init(frame: CGRect) {
        super.init(frame: frame)
        contentView.backgroundColor = .white
        isUserInteractionEnabled = false
        contentView.addSubview(container)
        container.snp.makeConstraints({m in
            m.edges.equalTo(contentView).inset(UIEdgeInsetsMake(0, 0, ProfileHeader.scrollViewHeight, 0))
        })

        addProfilePhoto()
        addUsernameLabel()
        addProfileDescription()
        addLikes()

        addTopContainer()
        addFollowingButton()
        addAccountButton()

        addScrollView()
        addSeparator()
        addSeparator1()
    }

    private func addAccountButton() {
        topContainer.addSubview(accountButton)
        accountButton.setTitle("Перейти в личный кабинет", for: .normal)
        accountButton.snp.makeConstraints({m in
            m.edges.equalTo(followButton)
        })
        accountButton.isHidden = true
        accountButton.addTarget(self, action: #selector(self.accountButtonClick), for: .touchUpInside)
    }

    func accountButtonClick() {
        let c = controller?.storyboard?.instantiateViewController(withIdentifier: "AccountController") as? AccountController
        c.map({ getController()?.navigationController?.pushViewController($0, animated: true)})
    }

    private func getController() -> UIViewController? {
        return (self.profileNewsController ?? self.controller) as? UIViewController
    }

    private func addFollowingButton() {
        topContainer.addSubview(followButton)
        followButton.snp.makeConstraints({m in
            m.left.equalTo(topContainer)
            m.right.equalTo(topContainer)
            m.top.equalTo(productsCount.snp.bottom).offset(5)
            m.height.equalTo(30)
        })
        followButton.addTarget(self, action: #selector(self.follow), for: .touchUpInside)
    }

    func follow() {
        self.profile.map({GlobalProvider.instance.getApiRequester().followingToggle(profileId: $0.id, completionHandler: {
            self.profile.map({
                GlobalProvider.instance.getApiRequester().getProfile(profileId: $0.id, completionHandler: { p in
                    self.profile = p
                    self.controller?.profile = p
                    self.render(profile: p, controller: self.controller, profileNewsController: self.profileNewsController)
                })
            })
        })})
        self.profile?.doIFollow = !(profile?.doIFollow ?? false)
        renderFollowButton(profile: self.profile)
    }

    private func addTopContainer() {
        contentView.addSubview(topContainer)
        topContainer.snp.makeConstraints({ m in
            m.top.equalTo(profilePhoto)
            m.left.equalTo(profilePhoto.snp.right).offset(20)
            m.right.equalTo(contentView).inset(10)
            m.height.equalTo(imgHeight)
        })

        topContainer.addSubview(productsCount)
        productsCount.descr.text = "товаров"
        productsCount.snp.makeConstraints({m in
            m.left.equalTo(topContainer)
            m.top.equalTo(topContainer)
            m.height.equalTo(imgHeight / 2)
            m.width.equalTo(topContainer).dividedBy(3)
        })

        topContainer.addSubview(followingsCount)
        followingsCount.descr.text = "подписок"
        followingsCount.snp.makeConstraints({ m in
            m.top.equalTo(productsCount)
            m.bottom.equalTo(productsCount)
            m.width.equalTo(productsCount)
            m.left.equalTo(productsCount.snp.right)
        })
        followingsCount.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(followingsCountClick)))

        topContainer.addSubview(followersCount)
        followersCount.descr.text = "подписчиков"
        followersCount.snp.makeConstraints({ m in
            m.top.equalTo(productsCount)
            m.bottom.equalTo(productsCount)
            m.width.equalTo(productsCount)
            m.left.equalTo(followingsCount.snp.right)
        })
        followersCount.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(followersCountClick)))
    }

    func followingsCountClick() {
        let c = controller?.storyboard?.instantiateViewController(withIdentifier: "AccountFollowingsController") as? AccountFollowingsController
        c?.profile = controller?.profile
        c?.parentController = controller
        c?.isFollowings = true
        c.map({getController()?.navigationController?.pushViewController($0, animated: true)})
    }

    func followersCountClick() {
        let c = controller?.storyboard?.instantiateViewController(withIdentifier: "AccountFollowingsController") as? AccountFollowingsController
        c?.profile = controller?.profile
        c?.parentController = controller
        c?.isFollowings = false
        c.map({getController()?.navigationController?.pushViewController($0, animated: true)})
    }

    private func addLikes() {
        contentView.addSubview(likes)
        likes.font = profileDescription.font
        likes.textColor = profileDescription.textColor
        likes.snp.makeConstraints({ m in
            m.left.equalTo(profileDescription)
            m.top.equalTo(profileDescription.snp.bottom).offset(2)
        })
    }

    private func addProfileDescription() {
        profileDescription.font = BoldFont.systemFont(ofSize: 13)
        profileDescription.textColor = .lightGray
        contentView.addSubview(profileDescription)
        profileDescription.snp.makeConstraints({ m in
            m.left.equalTo(usernameLabel)
            m.top.equalTo(usernameLabel.snp.bottom).offset(2)
        })
    }

    private func addSeparator1() {
        contentView.addSubview(separator1)
        separator1.backgroundColor = AppColors.separator()
        separator1.snp.makeConstraints({ m in
            m.height.equalTo(1)
            m.left.equalTo(contentView)
            m.right.equalTo(contentView)
            m.top.equalTo(scrollView)
        })
        separator1.layer.zPosition = 10
    }

    private func addSeparator() {
        contentView.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints({ m in
            m.height.equalTo(1)
            m.left.equalTo(contentView)
            m.right.equalTo(contentView)
            m.bottom.equalTo(scrollView)
        })
        separator.layer.zPosition = -1
    }

    func render(profile: PublicProfile, controller: ProfileProductsController?, profileNewsController: ProfileNewsController?) {
        if let c = controller { items[c.selectedIndex].select() }
        self.controller = controller
        self.profile = profile
        self.profileNewsController = profileNewsController
        isUserInteractionEnabled = (profile != nil)
        profilePhoto.setDefaultImage()
        if let url = profile.avatar {
            profilePhoto.af_setImage(withURL: URL(string: ApiRequester.domain + url)!)
        }
        usernameLabel.text = profile.nickname
        profileDescription.text = profile.pro ? "Профессиональный продавец" : "Индивидуальный продавец"
        likes.text = "Лайки " + (profile.likesCount.map({return String($0)}) ?? "0")
        productsCount.count.text = profile.productsForSale.map({String($0)}) ?? "0"
        followingsCount.count.text = profile.followingsCount.map({String($0)}) ?? "0"
        followersCount.count.text = profile.followersCount.map({String($0)}) ?? "0"
        renderFollowButton(profile: profile)
        accountButton.isHidden = !(profile.myProfile ?? false)
    }

    private func renderFollowButton(profile: PublicProfile?) {
        followButton.render(doIFollow: profile?.doIFollow)
        followButton.isHidden = profile?.myProfile ?? false
    }

    private func addScrollView() {
        contentView.addSubview(scrollView)
        scrollView.contentSize = CGSize(width: CGFloat(itemTitles.count) * itemWidth, height: ProfileHeader.scrollViewHeight)
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.snp.makeConstraints({ m in
            m.height.equalTo(ProfileHeader.scrollViewHeight)
            m.bottom.equalTo(contentView)
            m.left.equalTo(contentView)
            m.right.equalTo(contentView)
        })
        var offset: CGFloat = 0
        itemTitles.forEach({
            let item = ProfileHeaderItem(frame: CGRect(x: offset, y: 0, width: itemWidth, height: ProfileHeader.scrollViewHeight))
            item.setTitle($0, for: .normal)
            scrollView.addSubview(item)
            offset += self.itemWidth
            items.append(item)
            item.addTarget(self, action: #selector(self.selectItem), for: .touchUpInside)
        })
    }

    private func addUsernameLabel() {
        container.addSubview(usernameLabel)
        usernameLabel.snp.makeConstraints({m in
            m.left.equalTo(profilePhoto)
            m.top.equalTo(profilePhoto.snp.bottom).offset(5)
        })
//        usernameLabel.text = "ЕВГЕНИЯ"
        usernameLabel.textAlignment = .center
        usernameLabel.font = BlackFont.systemFont(ofSize: 16)
    }

    private func addProfilePhoto() {
        container.addSubview(profilePhoto)
        profilePhoto.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(imgHeight)
            make.height.equalTo(imgHeight)
            make.top.equalTo(container).inset(10)
            make.left.equalTo(container).inset(10)
        }
        profilePhoto.layer.cornerRadius = imgHeight / 2
    }

    func selectItem(_ sender: ProfileHeaderItem) {
        select(item: sender)
        if let index = items.index(where: {$0 == sender}) {
            controller?.selectedIndex = index
            controller?.clearData()
            controller?.loadData()
            profileNewsController?.switchTab(selectedIndex: index)
        }
    }

    private func select(item: ProfileHeaderItem) {
        items.forEach({
            $0.deSelect()
        })
        item.select()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

class TopItem : UIView {

    let count = UILabel()
    let descr = UILabel()

    override init(frame: CGRect) {
        super.init(frame: frame)
        isUserInteractionEnabled = true
        addCount()
        addDescr()
    }

    private func addDescr() {
        addSubview(descr)
        descr.textAlignment = .center
        descr.font = MediumFont.systemFont(ofSize: 14)
        descr.snp.makeConstraints({ m in
            m.centerX.equalTo(self)
            m.top.equalTo(count.snp.bottom).offset(2)
        })
    }

    private func addCount() {
        addSubview(count)
        count.font = BlackFont.systemFont(ofSize: 15)
        count.text = "0"
        count.textAlignment = .center
        count.snp.makeConstraints({ m in
            m.centerX.equalTo(self)
            m.top.equalTo(self)
        })
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}