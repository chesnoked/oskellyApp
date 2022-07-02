//
// Created by Виталий Хлудеев on 26.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogRow : CatalogBasicRow {
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        catalogNameLabel.font = MediumFont.systemFont(ofSize: 15)
        let container = UIView()
        contentView.addSubview(container)
        container.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(contentView)
            make.width.equalTo(contentView.snp.height)
            make.top.equalTo(contentView)
            make.right.equalTo(contentView)
        }
        let nextCatalogImageView = UIImageView(image: UIImage(named: "assets/images/catalog/CatalogNext.png"))
        container.addSubview(nextCatalogImageView)
        nextCatalogImageView.snp.makeConstraints { (make) -> Void in
            make.center.equalTo(container)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
