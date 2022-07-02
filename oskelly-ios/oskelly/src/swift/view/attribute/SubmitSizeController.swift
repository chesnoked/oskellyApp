//
// Created by Виталий Хлудеев on 13.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitSizeController : UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {

    var sizes: [Size] = []

    let picker = UIPickerView()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.picker.delegate = self
        self.picker.dataSource = self
        view.addSubview(picker)
        picker.snp.makeConstraints { m in
            m.edges.equalTo(view)
        }
        self.navigationItem.title = "Размер"
    }

    // The number of columns of data
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 2
    }

    // The number of rows of data
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if(component == 0) {
            return sizes.count
        }
        else {
            return sizes[picker.selectedRow(inComponent: 0)].values.count
        }
    }

    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if(component == 0) {
            picker.reloadComponent(1)
        }
    }

    // The data to return for the row and component (column) that's being passed in
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if(component == 0) {
            return sizes[row].type
        }
        else {
            return sizes[picker.selectedRow(inComponent: 0)].values[row].name
        }
    }
}