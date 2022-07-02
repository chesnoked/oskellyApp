//
// Created by Виталий Хлудеев on 24.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class Utils {

    static func getCrossedText(text: String) -> NSMutableAttributedString {
        let attributeString: NSMutableAttributedString =  NSMutableAttributedString(string: text)
        attributeString.addAttribute(NSStrikethroughStyleAttributeName, value: 2, range: NSMakeRange(0, attributeString.length))
        return attributeString
    }

    static func attributedText(withString string: String, boldString: String, font: UIFont, boldFont: UIFont) -> NSAttributedString {
        let attributedString = NSMutableAttributedString(string: string,
                attributes: [NSFontAttributeName: font])
        let boldFontAttribute: [String: Any] = [NSFontAttributeName: boldFont]
        let range = (string as NSString).range(of: boldString)
        attributedString.addAttributes(boldFontAttribute, range: range)
        return attributedString
    }
}