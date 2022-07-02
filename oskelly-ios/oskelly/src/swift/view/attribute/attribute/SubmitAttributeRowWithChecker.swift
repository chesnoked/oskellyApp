//
// Created by Виталий Хлудеев on 12.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitAttributeRowWithChecker : SubmitAttributeRow {
    let selectedStateImage = UIImage(named: "assets/images/catalog/Cheker.png")
    let unSelectedStateImage = UIImage(named: "assets/images/catalog/Knob.png")
    let selectStateImageView = UIImageView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectStateImageView.image = unSelectedStateImage
        contentView.addSubview(selectStateImageView)
        selectStateImageView.snp.makeConstraints({m in
            m.centerY.equalTo(contentView)
            m.left.equalTo(contentView).inset(10)
            m.width.equalTo(30)
            m.height.equalTo(30)
        })
        attributeNameLabel.snp.remakeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView)
            make.left.equalTo(selectStateImageView.snp.right).offset(10)

        }
        selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        if(selected) {
            selectStateImageView.image = selectedStateImage
        }
        else {
            selectStateImageView.image = unSelectedStateImage
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}