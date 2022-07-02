//
// Created by Виталий Хлудеев on 06.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CartAndOrderBaseRow : UITableViewCell {

    let productImageView = UIImageView()
    let brandLabel = UILabel()
    let descriptionLabel = UILabel()
    let sizeLabel = UILabel()
    let priceLabel = UILabel()
    let separator = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(productImageView)
        contentView.addSubview(brandLabel)
        contentView.addSubview(descriptionLabel)
        contentView.addSubview(sizeLabel)
        contentView.addSubview(priceLabel)
        contentView.addSubview(separator)
        productImageView.contentMode = .scaleAspectFit
        productImageView.snp.makeConstraints { make -> Void in
            make.left.equalTo(contentView).inset(10)
            make.bottom.equalTo(contentView).inset(7)
            make.top.equalTo(contentView).inset(7)
            make.width.equalTo(productImageView.snp.height)
        }
        brandLabel.font = BlackFont.systemFont(ofSize: 13)
        brandLabel.snp.makeConstraints { make -> Void in
            make.left.equalTo(productImageView.snp.right).offset(12)
            make.top.equalTo(contentView).inset(30)
        }
        descriptionLabel.font = MediumFont.systemFont(ofSize: 9)
        descriptionLabel.textColor = AppColors.lightGray()
        descriptionLabel.snp.makeConstraints { make -> Void in
            make.left.equalTo(brandLabel)
            make.top.equalTo(brandLabel.snp.bottom).offset(4)
        }
        sizeLabel.font = descriptionLabel.font
        sizeLabel.textColor = descriptionLabel.textColor
        sizeLabel.snp.makeConstraints { make in
            make.left.equalTo(descriptionLabel)
            make.top.equalTo(descriptionLabel.snp.bottom).offset(2)
        }
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { make in
            make.width.equalTo(contentView)
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
        }
        priceLabel.font = brandLabel.font
        priceLabel.snp.makeConstraints { make in
            make.centerY.equalTo(contentView)
            make.right.equalTo(contentView).inset(15)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}