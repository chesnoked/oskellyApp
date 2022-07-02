//
// Created by Виталий Хлудеев on 06.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderRow : CartAndOrderBaseRow {

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    public func render(orderPosition: OrderPosition) {

        let placeholderImage = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")
        if let imageUrl = orderPosition.imageUrl {
            let url = URL(string: ApiRequester.domain + imageUrl)!
            productImageView.af_setImage(withURL: url, placeholderImage: placeholderImage)
        }
        else {
            productImageView.image = placeholderImage
        }
//        sizeLabel.text = "Размер: " + cartItem.productSize!
        descriptionLabel.text = orderPosition.productName
        priceLabel.text = String(orderPosition.productPrice!) + " Р"
        brandLabel.text = orderPosition.brandName
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}