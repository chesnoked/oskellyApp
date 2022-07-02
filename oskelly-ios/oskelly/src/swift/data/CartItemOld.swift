//
//  CartItemOld.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 07.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class CartItemOld {
    
    let id: Int
    let imageUrl: String
    let brandName: String
    let productName: String
    let sizeDescription: String
    let currentPrice: Decimal
    
    init(id: Int,
         imageUrl: String,
         brandName: String,
         productName: String,
         sizeDescription: String,
         currentPrice: Decimal) {
        self.id = id
        self.brandName = brandName
        self.productName = productName
        self.sizeDescription = sizeDescription
        self.currentPrice = currentPrice
        self.imageUrl = imageUrl
    }
}
