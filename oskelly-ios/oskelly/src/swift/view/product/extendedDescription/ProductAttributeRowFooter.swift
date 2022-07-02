//
//  ProductAttributeRowFooter.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 04.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductAttributeRowFooter: UITableViewCell {
    
    let label = UILabel()
    let separator = UIView()
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.backgroundColor = UIColor(red: 248/255, green: 248/255, blue: 248/255, alpha: 1)
        contentView.addSubview(label)
        contentView.addSubview(separator)
        label.text = "Все товары предоставляются в одном экземпляре и только для розничной торговли."
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.font = MediumFont.systemFont(ofSize: 9)
        label.textColor = UIColor(red: 126/255, green: 126/255, blue: 126/255, alpha: 1)
        label.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(contentView).multipliedBy(0.57)
            make.left.equalTo(contentView).inset(20)
            make.top.equalTo(contentView).inset(20)
        }
        separator.backgroundColor = UIColor(red: 226/255, green: 226/255, blue: 226/255, alpha: 1)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1.0)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
