//
// Created by Виталий Хлудеев on 14.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountDateField : AccountTextField {

    let datePicker = UIDatePicker()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        showDatePicker()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func render(name: String, value: String?, numbersOnly: Bool, completeHandler: @escaping (Account) -> ()) {
        super.render(name: name, value: value, numbersOnly: numbersOnly, completeHandler: completeHandler)
        if let v = value {
            datePicker.date = GlobalProvider.instance.dateTimeConverter.fromDate(dateString: v)
            textField.text = GlobalProvider.instance.dateTimeConverter.fromDate(dateString: v, format: "dd MMMM yyyy")
        }
    }

    func showDatePicker(){
        datePicker.datePickerMode = .date
        datePicker.locale = Locale(identifier: "ru")

        let toolbar = UIToolbar();
        toolbar.sizeToFit()

        let doneButton = UIBarButtonItem(title: "OK", style: UIBarButtonItemStyle.bordered, target: self, action: "donedatePicker")
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.flexibleSpace, target: nil, action: nil)
        let cancelButton = UIBarButtonItem(title: "Отмена", style: UIBarButtonItemStyle.bordered, target: self, action: "cancelDatePicker")
        toolbar.setItems([cancelButton,spaceButton,doneButton], animated: false)

        textField.inputAccessoryView = toolbar
        textField.inputView = datePicker

    }

    func donedatePicker(){
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMMM yyyy"
        formatter.locale = Locale(identifier: "ru")
        textField.text = formatter.string(from: datePicker.date)
        self.textField.endEditing(true)
    }

    func cancelDatePicker(){
        self.textField.endEditing(true)
    }

    override func getValue() -> String? {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: datePicker.date)
    }
}