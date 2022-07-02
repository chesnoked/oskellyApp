//
// Created by Виталий Хлудеев on 16.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class Brand: NSObject, NSCoding {

    let id: Int
    let name: String
    var checked = false

    init(id: Int, name: String) {
        self.id = id
        self.name = name
    }

    required init(coder aDecoder: NSCoder) {
        id = aDecoder.decodeInteger(forKey: "id") as Int
        name = aDecoder.decodeObject(forKey: "name") as! String
    }

    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "id")
        aCoder.encode(name, forKey: "name")
    }
}