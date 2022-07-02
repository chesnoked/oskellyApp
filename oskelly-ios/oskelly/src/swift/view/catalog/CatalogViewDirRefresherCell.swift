//
//  CatalogViewDirRefresherCell.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 31.05.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogViewDirRefresherCell: UICollectionViewCell {
    
    let refresher = UIActivityIndicatorView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        contentView.addSubview(refresher)
        refresher.color = UIColor.lightGray
        refresher.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(contentView)
        }
    }
    
    func showRefresher () {
        refresher.show()
        refresher.startAnimating()
    }

    func hideRefresher() {
        refresher.hide()
        refresher.stopAnimating()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
