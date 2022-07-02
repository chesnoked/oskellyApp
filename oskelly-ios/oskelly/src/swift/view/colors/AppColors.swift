//
//  AppColors.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 06.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class AppColors {
    
    static func lightGray() -> UIColor {
        return UIColor.lightGray
    }
    
    static func separator() -> UIColor {
        return UIColor(red: 214/255, green: 213/255, blue: 217/255, alpha: 1)
    }
    
    static func darkButton() -> UIColor {
        return UIColor(red: 30/255, green: 30/255, blue: 30/255, alpha: 1)
    }
    
    static func lato() -> UIColor {
        return UIColor(red: 207/255, green: 167/255, blue: 136/255, alpha: 1)
    }

    static func latoTrans() -> UIColor {
        return UIColor(red: 207/255, green: 167/255, blue: 136/255, alpha: 0.6)
    }

    static func transparent() -> UIColor {
        return UIColor(red: 239/255, green: 239/255, blue: 244/255, alpha: 1)
    }

    static func textField() -> UIColor {
        return UIColor(red: 76/255, green: 76/255, blue: 76/255, alpha: 1)
    }

    static func textFieldTrans() -> UIColor {
        return UIColor(red: 76/255, green: 76/255, blue: 76/255, alpha: 0.6)
    }

    static func grayLabel() -> UIColor {
        return UIColor(red: 236/255, green: 236/255, blue: 236/255, alpha: 1)
    }

    static func grayLabelText() -> UIColor {
        return UIColor(red: 86/255, green: 86/255, blue: 86/255, alpha: 1)
    }
}
