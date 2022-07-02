//
// Created by Виталий Хлудеев on 14.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountTextField : BaseTextFieldView {

    var completeHandler: ((Account) -> ())!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    func render(name: String, value: String?, numbersOnly: Bool, completeHandler: @escaping (Account) -> ()) {
        super.render(name: name, value: value, numbersOnly: numbersOnly)
        self.completeHandler = completeHandler
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public func textFieldDidEndEditing(_ textField: UITextField) {
        let account = GlobalProvider.instance.accountProvider.getCurrent(completionHandler: {a in
            self.completeHandler(a)
            GlobalProvider.instance.getApiRequester().saveAccount(account: a)
            GlobalProvider.instance.accountProvider.setCurrent(account: a)
        }, needSynchronize: false)
    }
}