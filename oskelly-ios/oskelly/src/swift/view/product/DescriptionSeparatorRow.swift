//
//  DescriptionSeparatorRow.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 03.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class DescriptionSeparatorRow : UITableViewCell {
    
    let topViewSeparotor = UIView()
    let bottomSeparotor = UIView()
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        let separator = UIView()
        contentView.addSubview(separator)
        separator.backgroundColor = UIColor(red: 239/255, green: 239/255, blue: 244/255, alpha: 1)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(12)
            make.edges.equalTo(contentView)
        }
        
        topViewSeparotor.backgroundColor = UIColor(red: 226/255, green: 226/255, blue: 226/255, alpha: 1)
        separator.addSubview(topViewSeparotor)
        bottomSeparotor.backgroundColor = topViewSeparotor.backgroundColor
        separator.addSubview(bottomSeparotor)
        topViewSeparotor.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.width.equalTo(separator.snp.width)
            make.top.equalTo(separator)
        }
        bottomSeparotor.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.width.equalTo(separator.snp.width)
            make.bottom.equalTo(separator)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
