//
//  ProductAttributeRowHeader.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 04.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import SnapKit
import UIKit

class ProductAttributeRowHeader: UITableViewCell {
    
    let desctiptionLabel = UILabel()
    let desctiptionTitleLabel = UILabel()
    let separator = UIView()
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(desctiptionLabel)
        contentView.addSubview(desctiptionTitleLabel)
        contentView.addSubview(separator)
        separator.backgroundColor = UIColor(red: 226/255, green: 226/255, blue: 226/255, alpha: 1)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1.0)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)//.inset(20)
        }
        contentView.backgroundColor = UIColor(red: 248/255, green: 248/255, blue: 248/255, alpha: 1)
        desctiptionLabel.text = "Наши специалисты не только проверяют вещь на подлинность, но и оценивают, насколько она соответствует описанию продавца. На экспертизе уделяется внимание каждой детали: размеру, цвету, модели и общему состоянию вещи."
        desctiptionLabel.lineBreakMode = .byWordWrapping
        desctiptionLabel.textAlignment = .center
        desctiptionLabel.numberOfLines = 0
        desctiptionLabel.font = MediumFont.systemFont(ofSize: 11)
        desctiptionLabel.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(contentView).inset(UIEdgeInsetsMake(40, 20, 40, 20))
        }
        desctiptionTitleLabel.textAlignment = .center
        desctiptionTitleLabel.text = "Контроль качества"
        desctiptionTitleLabel.font = BlackFont.systemFont(ofSize: 11)
        desctiptionTitleLabel.snp.makeConstraints { (make) -> Void in
            make.centerX.equalTo(contentView)
            make.height.equalTo(20)
            make.bottom.equalTo(desctiptionLabel.snp.top)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
