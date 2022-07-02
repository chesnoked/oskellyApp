//
// Created by Виталий Хлудеев on 01.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CreditCardRow : BaseTextFieldView {

    var completionHandler: ((Card) -> ())!

    var maxLength: Int = 20

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    func render(name: String, numbersOnly: Bool, placeholder: String, maxLength: Int, completionHandler: @escaping (Card) -> ()) {
        super.render(name: name, value: nil, numbersOnly: numbersOnly)
        self.completionHandler = completionHandler
        self.maxLength = maxLength
        let attributes = [
            NSForegroundColorAttributeName: UIColor(red: 216/255, green: 216/255, blue: 216/255, alpha: 1),
            NSFontAttributeName : MediumFont.systemFont(ofSize: 12)
        ]
        textField.attributedPlaceholder = NSAttributedString(string: placeholder, attributes:attributes)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }


    public override func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool  {
        let maxLength = self.maxLength
        let currentString: NSString = textField.text! as NSString
        let newString: NSString = currentString.replacingCharacters(in: range, with: string) as NSString
        return newString.length <= maxLength && super.textField(textField, shouldChangeCharactersIn: range, replacementString: string)
    }

    override func onChange() {
        let paymentRequest = GlobalProvider.instance.paymentRequestProvider.getCurrent()
        if(paymentRequest.card == nil) {
            paymentRequest.card = Card()
        }
        if(paymentRequest.card!.expiryDate == nil) {
            paymentRequest.card!.expiryDate = ExpiryDate()
        }
        completionHandler(paymentRequest.card!)
        GlobalProvider.instance.paymentRequestProvider.setCurrent(paymentRequest: paymentRequest)
    }
}