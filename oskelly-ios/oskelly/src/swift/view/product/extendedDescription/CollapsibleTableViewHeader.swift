//
//  CollapsibleTableViewHeader.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 02.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class CollapsibleTableViewHeader: UITableViewHeaderFooterView {
    let titleLabel = UILabel()
    let separator = UIView()
    let imageViewContainer = UIView()
    let collapsedImage = UIImage(named: "assets/images/product/Collapsed.png")
    let expandedImage = UIImage(named: "assets/images/product/Expanded.png")
    var imageView = UIImageView()
    
    var delegate: CollapsibleTableViewHeaderDelegate?
    var section: Int = 0
    
    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        imageView = UIImageView(image: collapsedImage)

        
        contentView.addSubview(titleLabel)
        contentView.addSubview(separator)
        contentView.addSubview(imageViewContainer)
        imageViewContainer.addSubview(imageView)
        
        titleLabel.textAlignment = .center
        titleLabel.backgroundColor = .white
        titleLabel.font = MediumFont.systemFont(ofSize: 15)
        titleLabel.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(contentView)
        }
        
        separator.backgroundColor = UIColor(red: 226/255, green: 226/255, blue: 226/255, alpha: 1)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.width.equalTo(contentView)
            make.bottom.equalTo(contentView)
        }
        
        imageViewContainer.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(contentView.snp.height)
            make.height.equalTo(contentView)
            make.right.equalTo(contentView)
        }
        
        imageView.snp.makeConstraints { (make) -> Void in
            make.center.equalTo(imageViewContainer)
        }
        
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(CollapsibleTableViewHeader.tapHeader(gestureRecognizer:))))
    }
    
    func tapHeader(gestureRecognizer: UITapGestureRecognizer) {
        guard let cell = gestureRecognizer.view as? CollapsibleTableViewHeader else {
            return
        }
        delegate?.toggleSection(header: self, section: cell.section)
    }

    func setCollapsed(collapsed: Bool) {
        separator.isHidden = !collapsed
        imageView.image = collapsed ? collapsedImage : expandedImage
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
