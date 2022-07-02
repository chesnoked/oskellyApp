//
//  CartRow.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 06.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CartRow : CartAndOrderBaseRow {
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
    }
    
    public func render(cartItem: CartItem) {

        let placeholderImage = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")
        if let imageUrl = cartItem.imageUrl {
            let url = URL(string: ApiRequester.domain + imageUrl)!
            productImageView.af_setImage(withURL: url, placeholderImage: placeholderImage)
        }
        else {
            productImageView.image = placeholderImage
        }
//        sizeLabel.text = "Размер: " + cartItem.productSize!
        descriptionLabel.text = cartItem.productName
        priceLabel.text = String(cartItem.productPrice!) + " Р"
        brandLabel.text = cartItem.brandName
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
