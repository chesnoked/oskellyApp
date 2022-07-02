//
// Created by Виталий Хлудеев on 12.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSortRow : CatalogBasicRow {

    private let checkmark = UIImageView(image: UIImage(named: "assets/images/catalog/Checkmark.png"))

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        catalogNameLabel.font = MediumFont.systemFont(ofSize: 14)
        contentView.addSubview(checkmark)
        checkmark.snp.makeConstraints({m in
            m.centerY.equalTo(contentView)
            m.right.equalTo(contentView).inset(10)
        })
        selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        checkmark.isHidden = !selected
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}