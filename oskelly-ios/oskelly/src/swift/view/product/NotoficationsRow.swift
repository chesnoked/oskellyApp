//
// Created by Виталий Хлудеев on 24.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class NotificationsRow : UITableViewCell {

    let wishListAddButton = UIButton()
    let notificationView = UIView()
    let alertContainer = UIView()
    let wishListLabel = UILabel()
    let priceFollowingLabel = UILabel()
    let priceFollowingButton = UIButton()

    var controller: UIViewController!
    var product: Product!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addNotificationView(container: contentView)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(product: Product, controller: UIViewController) {
        self.controller = controller
        self.product = product
        wishListButtonToggle(product: product)
        priceFollowingButtonToggle(product: product)
    }

    func addNotificationView(container: UIView) {
        container.addSubview(notificationView)
        notificationView.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(container).inset(UIEdgeInsetsMake(35, 0, 0, 0))
            make.height.equalTo(container.bounds.width / 5)
        }

        let wishListContainer = UIView()
        notificationView.addSubview(wishListContainer)
        wishListContainer.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(notificationView.snp.width).dividedBy(2)
            make.height.equalTo(notificationView.snp.height)
            make.left.equalTo(notificationView.snp.left)
            make.top.equalTo(notificationView.snp.top)
        }

        addWishListButton(wishListContainer: wishListContainer)

        wishListLabel.text = "Добавить в Wish-list"
        wishListLabel.textColor = .lightGray
        wishListLabel.textAlignment = .center
        wishListLabel.numberOfLines = 0
        wishListLabel.lineBreakMode = .byWordWrapping
        wishListLabel.font = UIFont.boldSystemFont(ofSize: 10)
        wishListContainer.addSubview(wishListLabel)
        wishListLabel.snp.makeConstraints { (make) -> Void in
            make.centerX.equalTo(wishListContainer.snp.centerX)
            make.top.equalTo(wishListAddButton.snp.bottom).offset(10)
            make.width.equalTo(wishListContainer.snp.width).multipliedBy(0.9)
        }

        let verticalSeparator = UIView()
        verticalSeparator.backgroundColor = .lightGray
        wishListContainer.addSubview(verticalSeparator)
        verticalSeparator.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(1)
            make.height.equalTo(wishListContainer.snp.height).multipliedBy(0.8)
            make.right.equalTo(wishListContainer.snp.right)
            make.top.equalTo(wishListContainer.snp.top)
        }

        let priceFollowingContainer = UIView()
        notificationView.addSubview(priceFollowingContainer)
        priceFollowingContainer.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(wishListContainer.snp.width)
            make.height.equalTo(wishListContainer.snp.height)
            make.left.equalTo(wishListContainer.snp.right)
            make.top.equalTo(wishListContainer.snp.top)
        }

        addPriceFollowingButton(wishListContainer: wishListContainer, priceFollowingContainer: priceFollowingContainer)

        priceFollowingLabel.text = "Следить за ценой"
        priceFollowingLabel.textColor = .lightGray
        priceFollowingLabel.textAlignment = .center
        priceFollowingLabel.numberOfLines = 0
        priceFollowingLabel.lineBreakMode = .byWordWrapping
        priceFollowingLabel.font = UIFont.boldSystemFont(ofSize: 10)
        wishListContainer.addSubview(priceFollowingLabel)
        priceFollowingLabel.snp.makeConstraints { (make) -> Void in
            make.centerX.equalTo(priceFollowingContainer.snp.centerX)
            make.top.equalTo(priceFollowingButton.snp.bottom).offset(10)
            make.width.equalTo(priceFollowingContainer.snp.width).multipliedBy(0.9)
        }

        let verticalSeparator2 = UIView()
        verticalSeparator2.backgroundColor = .lightGray
        priceFollowingContainer.addSubview(verticalSeparator2)
        verticalSeparator2.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(1)
            make.height.equalTo(priceFollowingContainer.snp.height).multipliedBy(0.8)
            make.right.equalTo(priceFollowingContainer.snp.right)
            make.top.equalTo(priceFollowingContainer.snp.top)
        }

        notificationView.addSubview(alertContainer)
        alertContainer.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(wishListContainer.snp.width)
            make.height.equalTo(wishListContainer.snp.height)
            make.left.equalTo(priceFollowingContainer.snp.right)
            make.top.equalTo(wishListContainer.snp.top)
        }

        let alertButton = UIButton()
        alertButton.setImage(UIImage(named: "assets/images/product/Alarm.png"), for: .normal)
        alertButton.imageView?.contentMode = .scaleAspectFit
        alertContainer.addSubview(alertButton)
        alertButton.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(alertContainer.snp.top)
            make.centerX.equalTo(alertContainer.snp.centerX)
        }

        let alertLabel = UILabel()
        alertLabel.text = "Создать оповещение"
        alertLabel.textColor = .lightGray
        alertLabel.textAlignment = .center
        alertLabel.numberOfLines = 0
        alertLabel.lineBreakMode = .byWordWrapping
        alertLabel.font = UIFont.boldSystemFont(ofSize: 10)
        alertContainer.addSubview(alertLabel)
        alertLabel.snp.makeConstraints { (make) -> Void in
            make.centerX.equalTo(alertContainer.snp.centerX)
            make.top.equalTo(alertButton.snp.bottom).offset(10)
            make.width.equalTo(alertContainer.snp.width).multipliedBy(0.9)
        }
        alertContainer.isHidden = true
        verticalSeparator2.isHidden = true
    }

    private func addPriceFollowingButton(wishListContainer: UIView, priceFollowingContainer: UIView) {
        priceFollowingButton.setImage(UIImage(named: "assets/images/product/PriceFollowing.png"), for: .normal)
        priceFollowingButton.imageView?.contentMode = .scaleAspectFit
        priceFollowingContainer.addSubview(priceFollowingButton)
        priceFollowingButton.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(priceFollowingContainer.snp.top)
            make.centerX.equalTo(priceFollowingContainer.snp.centerX)
        }
        priceFollowingButton.addTarget(self, action: #selector(self.priceFollowingToggle), for: .touchUpInside)
    }

    private func addWishListButton(wishListContainer: UIView) {
        wishListAddButton.setImage(UIImage(named: "assets/images/product/Star.png"), for: .normal)
        wishListAddButton.imageView?.contentMode = .scaleAspectFit
        wishListContainer.addSubview(wishListAddButton)
        wishListAddButton.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(wishListContainer.snp.top)
            make.centerX.equalTo(wishListContainer.snp.centerX)
        }
        wishListAddButton.addTarget(self, action: #selector(self.wishListToggle), for: .touchUpInside)
    }

    func wishListToggle() {
        GlobalProvider.instance.getApiRequester().wishListToggle(productId: product.id)
        product.inMyWishList = !product.inMyWishList
        wishListButtonToggle(product: product)
    }

    func priceFollowingToggle() {
        GlobalProvider.instance.getApiRequester().priceFollowingToggle(productId: product.id)
        product.doIWatchOutForPrice = !product.doIWatchOutForPrice
        priceFollowingButtonToggle(product: product)
    }

    private func priceFollowingButtonToggle(product: Product) {
        priceFollowingButton.setImage(UIImage(named: product.doIWatchOutForPrice ? "assets/images/product/BlackPriceFollowing.png" : "assets/images/product/PriceFollowing.png"), for: .normal)
        priceFollowingLabel.text =  product.doIWatchOutForPrice ? "Следим за ценой!" : "Следить за ценой"
    }

    private func wishListButtonToggle(product: Product) {
        wishListAddButton.setImage(UIImage(named: product.inMyWishList ? "assets/images/product/BlackStar.png" : "assets/images/product/Star.png"), for: .normal)
        wishListLabel.text =  product.inMyWishList ? "Добавлено!" : "Добавить в Wish-list"
    }
}