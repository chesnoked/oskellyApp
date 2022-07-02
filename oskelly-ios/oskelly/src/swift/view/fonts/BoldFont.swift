//
//  BoldFont.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 30.05.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class BoldFont: UIFont {
    override class func systemFont(ofSize fontSize: CGFloat) -> UIFont
    {
        return UIFont(name: "Lato-Bold", size: fontSize)!
    }
}

