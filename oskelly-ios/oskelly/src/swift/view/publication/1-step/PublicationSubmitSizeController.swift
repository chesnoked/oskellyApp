//
// Created by Виталий Хлудеев on 20.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationSubmitSizeController : SubmitSizeController {

    var draft: Draft!

    override func viewDidLoad() {
        super.viewDidLoad()
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "OK", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
    }

    // The data to return for the row and component (column) that's being passed in
    override func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if(component == 0) {
            if(sizes[row].type != nil && draft.selectedSizeType == sizes[row].type) {
                picker.selectRow(row, inComponent: 0, animated: false)
                picker.reloadComponent(1)
            }
            else if((draft.selectedSizeType == nil) && row == 0) {
                draft.selectedSizeType = sizes[row].type
                updateDraft()
            }
        }
        else {
            if let selectedSizeId = draft.selectedSize?.id {
                if(sizes[picker.selectedRow(inComponent: 0)].values[row].id == selectedSizeId) {
                    picker.selectRow(row, inComponent: 1, animated: false)
                }
            }
            else if((draft.selectedSize == nil || draft.selectedSize?.id == nil) && row == 0) {
                let value = sizes[picker.selectedRow(inComponent: 0)].values[row]
                draft.selectedSize = SelectedSize(id: value.id, value: value.name)
                updateDraft()
            }
        }
        return super.pickerView(pickerView, titleForRow: row, forComponent: component)
    }

    override func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        super.pickerView(pickerView, didSelectRow: row, inComponent: component)
        if(component == 0) {
            let value = sizes[picker.selectedRow(inComponent: 0)].values[picker.selectedRow(inComponent: 1)]
            draft.selectedSizeType = sizes[row].type
            draft.selectedSize = SelectedSize(id: value.id, value: value.name)
            updateDraft()
        }
        else if(component == 1) {
            let value = sizes[picker.selectedRow(inComponent: 0)].values[row]
            draft.selectedSize = SelectedSize(id: value.id, value: value.name)
            updateDraft()
        }
    }

    private func updateDraft() {
        GlobalProvider.instance.draftProvider.setCurrent(draft: draft)
        GlobalProvider.instance.draftProvider.publish(draft: draft) {d in}
    }

    func okPressed() {
        navigationController?.popViewController(animated: true)
    }
}