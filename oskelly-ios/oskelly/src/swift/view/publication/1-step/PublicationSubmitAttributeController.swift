//
// Created by Виталий Хлудеев on 18.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class PublicationSubmitAttributeController : SubmitAttributeController {

    var draft: Draft!

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        if let value: SelectedAttributeValues = draft.selectedAttributeValues!.filter({$0.attributeId! == attribute.id}).first {
            if attribute.values[indexPath.row].id == value.id {
                tableView.selectRow(at: indexPath, animated: false, scrollPosition: .top)
            }
        }

        return super.tableView(tableView, cellForRowAt: indexPath)
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let v = attribute.values[indexPath.row]
        draft.selectedAttributeValues = draft.selectedAttributeValues!.filter({$0.attributeId != attribute.id})
        draft.selectedAttributeValues!.append(SelectedAttributeValues(id: v.id, value: v.value, attributeId: v.attributeId, name: attribute.name))
        GlobalProvider.instance.draftProvider.setCurrent(draft: draft)
        GlobalProvider.instance.draftProvider.publish(draft: draft) { d in
        }
        navigationController?.popViewController(animated: true)
    }
}