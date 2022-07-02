//
// Created by Виталий Хлудеев on 27.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class ProductCondition: NSObject, NSCoding {
    let id : Int
    let name: String
    let descr: String

    init(
             id : Int,
             name: String,
             description: String
    ) {
        self.id = id
        self.name = name
        self.descr = description
    }

    required init(coder aDecoder: NSCoder) {
        id = aDecoder.decodeInteger(forKey: "id") as Int
        name = aDecoder.decodeObject(forKey: "name") as! String
        descr = aDecoder.decodeObject(forKey: "description") as! String
    }

    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "id")
        aCoder.encode(name, forKey: "name")
        aCoder.encode(descr, forKey: "description")
    }
}