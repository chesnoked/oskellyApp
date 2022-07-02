//
// Created by Виталий Хлудеев on 26.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationTextFieldView : BaseTextFieldView {

    var draft : Draft!

    var completionHandler: ((Draft) -> ())!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        draft = GlobalProvider.instance.draftProvider.getCurrent()
    }

    func render(name: String, value: String?, numbersOnly: Bool, completionHandler: @escaping (Draft) -> ()) {
        super.render(name: name, value: value, numbersOnly: numbersOnly)
        self.completionHandler = completionHandler
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public func textFieldDidEndEditing(_ textField: UITextField) {
        completionHandler(draft)
        GlobalProvider.instance.draftProvider.setCurrent(draft: draft)
        GlobalProvider.instance.draftProvider.publish(draft: draft) { d in}
    }
}