//
//  RegularFont.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 30.05.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class RegularFont: UIFont {
    
    override class func systemFont(ofSize fontSize: CGFloat) -> UIFont
    {
        return UIFont(name: "Lato-Regular", size: fontSize)!
    }
}
