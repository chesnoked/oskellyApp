//
// Created by Виталий Хлудеев on 25.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class ProductCategory: NSObject, NSCoding {

    let id: Int
    let name: String
    let displayInTreeName: String
    var children: [ProductCategory]

    init(
            id: Int,
            name: String,
            children: [ProductCategory],
            displayInTreeName: String
    ) {
        self.id = id
        self.name = name
        self.children = children
        self.displayInTreeName = displayInTreeName
    }

    required init(coder aDecoder: NSCoder) {
        id = aDecoder.decodeInteger(forKey: "id") as Int
        name = aDecoder.decodeObject(forKey: "name") as! String
        displayInTreeName = aDecoder.decodeObject(forKey: "displayInTreeName") as! String
        children = aDecoder.decodeObject(forKey: "children") as! [ProductCategory]
    }

    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "id")
        aCoder.encode(name, forKey: "name")
        aCoder.encode(displayInTreeName, forKey: "displayInTreeName")
        aCoder.encode(children, forKey: "children")
    }
}