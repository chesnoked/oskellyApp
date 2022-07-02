//
// Created by Виталий Хлудеев on 16.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitCategoryRow: UITableViewCell {

    let catalogNameLabel: UILabel = UILabel()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(catalogNameLabel)
        catalogNameLabel.snp.makeConstraints { (make) -> Void in
            make.center.equalTo(contentView)
        }
        catalogNameLabel.font = MediumFont.systemFont(ofSize: 13)
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

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}