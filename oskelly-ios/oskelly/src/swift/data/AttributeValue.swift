//
// Created by Виталий Хлудеев on 18.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class AttributeValue {
    let id: Int
    let value: String
    let attributeId: Int

    init(
            id: Int,
            value: String,
            attributeId: Int
    ) {
        self.id = id
        self.value = value
        self.attributeId = attributeId
    }
}