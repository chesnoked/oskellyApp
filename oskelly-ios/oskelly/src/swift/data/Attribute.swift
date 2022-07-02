//
// Created by Виталий Хлудеев on 18.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class Attribute {

    let id: Int
    let name: String
    var values: [AttributeValue]
    var selectedValue: AttributeValue?

    init(
            id: Int,
            name: String,
            values: [AttributeValue],
            selectedValue: AttributeValue?
    ) {
        self.id = id
        self.name = name
        self.values = values
        self.selectedValue = selectedValue
    }
}