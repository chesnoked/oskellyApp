//
// Created by Виталий Хлудеев on 15.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class DraftRow : UITableViewCell {

    let separator = UIView()

    var draftImage = UIImageView(image: UIImage(named: "assets/images/publication/DraftPlaceHolder.png"))

    var brand = UILabel()

    var category = UILabel()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.backgroundColor = .white
        contentView.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1.0)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
        contentView.addSubview(draftImage)
        draftImage.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(contentView).multipliedBy(0.8)
            make.width.equalTo(contentView.snp.height).multipliedBy(0.8)
            make.centerY.equalTo(contentView)
            make.left.equalTo(contentView).inset(10)
        }
        draftImage.contentMode = .scaleToFill
        contentView.addSubview(brand)
        brand.font = BlackFont.systemFont(ofSize: 13)
        brand.text = "BALENCIAGA"
        brand.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(draftImage)
            make.left.equalTo(draftImage.snp.right).offset(10)
        }
        contentView.addSubview(category)
        category.font = MediumFont.systemFont(ofSize: 10)
        category.text = "Кожаные сандалии"
        category.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(brand.snp.bottom).offset(6)
            make.left.equalTo(brand)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(draft: Draft) {
        let placeholderImage = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")
        if(!draft.images!.isEmpty) {
            if let imageUrl = draft.images?[0].url {
                let url = URL(string: imageUrl)!
                draftImage.af_setImage(withURL: url, placeholderImage: placeholderImage)
            }
        }
        else {
            draftImage.image = placeholderImage
        }
        brand.text = draft.brandName
        category.text = draft.categoryName
    }
}