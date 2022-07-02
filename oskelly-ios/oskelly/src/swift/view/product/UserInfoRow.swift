//
// Created by Виталий Хлудеев on 24.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class UserInfoRow : UITableViewCell {

    let userPhoto = ProfilePhotoView()
    let registrationDateLabel = UILabel()
    let proLabel = ProLabel()
    let sellerNameLabel = UILabel()
    let trustedImageView = UIImageView(image: UIImage(named: "assets/images/profile/Trusted.png"))
    let regionLabel = UILabel()
    let descr = UILabel()

    private var product: Product!
    private var controller: UIViewController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addUserInfoPanel(container: contentView)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func addUserInfoPanel(container: UIView) {
        let userInfoPanel = UIView()
        container.addSubview(userInfoPanel)
        let userInfoPanelHeight = UIScreen.main.bounds.width / 2
        userInfoPanel.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(container).inset(UIEdgeInsetsMake(30, 15, 0, 15))
//            make.height.equalTo(userInfoPanelHeight)
        }
        userInfoPanel.addSubview(userPhoto)
        let userPhotoHeight: CGFloat = 80
        userPhoto.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(userPhotoHeight)
            make.height.equalTo(userPhotoHeight)
            make.top.equalTo(userInfoPanel.snp.top)
            make.left.equalTo(userInfoPanel.snp.left)
        }
        userPhoto.layer.cornerRadius = userPhotoHeight / 2

        let sellerLabel = UILabel()
        sellerLabel.font = UIFont.boldSystemFont(ofSize: 10)
        sellerLabel.textColor = .lightGray
        sellerLabel.textAlignment = .left
        sellerLabel.text = "Продавец"
        userInfoPanel.addSubview(sellerLabel)
        sellerLabel.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(userPhoto.snp.right).offset(10)
        }

        sellerNameLabel.text = "ЕВГЕНИЯ"
        sellerNameLabel.font = BoldFont.systemFont(ofSize: 16)
        sellerNameLabel.textAlignment = .left
        userInfoPanel.addSubview(sellerNameLabel)
        sellerNameLabel.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(sellerLabel.snp.left)
            make.centerY.equalTo(userPhoto.snp.centerY)
            make.top.equalTo(sellerLabel.snp.bottom).offset(5)
        }

        userInfoPanel.addSubview(proLabel)
        proLabel.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(sellerLabel.snp.right).offset(5)
            make.centerY.equalTo(sellerLabel.snp.centerY)
        }

        regionLabel.text = "РОССИЯ, МОСКВА"
        regionLabel.font = BoldFont.systemFont(ofSize: 10)
        regionLabel.textAlignment = .left
        userInfoPanel.addSubview(regionLabel)
        regionLabel.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(sellerLabel.snp.left)
            make.top.equalTo(sellerNameLabel.snp.bottom).offset(5)
        }

        registrationDateLabel.text = "Присоединился к OSKELLY 26/03/2017"
        registrationDateLabel.textColor = .lightGray
        registrationDateLabel.font = UIFont.boldSystemFont(ofSize: 10)
        registrationDateLabel.textAlignment = .left
        userInfoPanel.addSubview(registrationDateLabel)
        registrationDateLabel.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(userInfoPanel.snp.left).offset(7)
            make.top.equalTo(userPhoto.snp.bottom).offset(35)
        }

        userInfoPanel.addSubview(trustedImageView)
        trustedImageView.snp.makeConstraints { (make) -> Void in
            make.centerY.equalTo(registrationDateLabel.snp.centerY)
            make.right.equalTo(userInfoPanel).inset(10)
        }

        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        userPhoto.isUserInteractionEnabled = true
        userPhoto.addGestureRecognizer(tapGestureRecognizer)

        let descrContainer = UIView()
        container.addSubview(descrContainer)
            descrContainer.backgroundColor = UIColor(red: 244/255, green: 244/255, blue: 244/255, alpha: 1)
        descrContainer.snp.makeConstraints({m in
            m.top.equalTo(registrationDateLabel.snp.bottom).offset(12)
            m.left.equalTo(container).inset(10)
            m.right.equalTo(container).inset(10)
            m.bottom.equalTo(container).inset(30)
        })
        descrContainer.addSubview(descr)
        descrContainer.layer.cornerRadius = 10
        descr.snp.makeConstraints({ m in
            m.edges.equalTo(descrContainer).inset(UIEdgeInsetsMake(15, 10, 30, 10))
        })
        descr.textColor = UIColor(red: 126/255, green: 126/255, blue: 126/255, alpha: 1)
        descr.numberOfLines = 0
        descr.font = MediumFont.systemFont(ofSize: 11)
    }

    func render(product: Product, controller: UIViewController) {
        self.product = product
        self.controller = controller
        if let url = product.avatar {
            userPhoto.af_setImage(withURL: URL(string: ApiRequester.domain + url)!) // FIXME: убрать слэш
        }
        sellerNameLabel.text = product.seller?.uppercased()
        proLabel.isHidden = !product.pro!
        registrationDateLabel.text = "Дата регистрации в OSKELLY " + GlobalProvider.instance.dateTimeConverter.fromDateTimeWithTimeZone(dateString: product.registrationDate!, format: "dd.MM.yyyy")
        trustedImageView.isHidden = true
        regionLabel.text = "РОССИЯ"
        descr.text = product.descriptionValue
    }

    func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
    {
        GlobalProvider.instance.navigator.navigateToProfile(profileId: product.sellerId!, controller: controller)
    }
}