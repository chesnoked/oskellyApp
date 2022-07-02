//
//  ProductAttributeRow.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 04.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductAttributeRow: UITableViewCell {
    
    let nameLabel = UILabel()
    let valueLabel = UILabel()
    let separator = UIView()
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(nameLabel)
        contentView.addSubview(valueLabel)
        contentView.addSubview(separator)
        contentView.backgroundColor = UIColor(red: 248/255, green: 248/255, blue: 248/255, alpha: 1)
        separator.backgroundColor = UIColor(red: 226/255, green: 226/255, blue: 226/255, alpha: 1)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1.0)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)//.inset(20)
        }
        nameLabel.textColor = UIColor(red: 126/255, green: 126/255, blue: 126/255, alpha: 1)
        nameLabel.font = MediumFont.systemFont(ofSize: 13)
        nameLabel.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(separator).inset(20)
            make.centerY.equalTo(contentView)
        }
        valueLabel.font = nameLabel.font
        valueLabel.textColor = UIColor(red: 30/255, green: 30/255, blue: 30/255, alpha: 1)
        valueLabel.snp.makeConstraints { (make) -> Void in
            make.right.equalTo(separator).inset(20)
            make.centerY.equalTo(contentView)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func render(productAttribute: ProductAttribute) {
        nameLabel.text = productAttribute.name! + ":"
        valueLabel.text = productAttribute.value
    }
}
