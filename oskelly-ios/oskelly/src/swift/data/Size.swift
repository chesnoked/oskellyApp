//
// Created by Виталий Хлудеев on 18.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class Size {

    let type: String
    var values: [SizeValue]

    init(
            type: String,
            values: [SizeValue]
    ) {
        self.type = type
        self.values = values
    }
}