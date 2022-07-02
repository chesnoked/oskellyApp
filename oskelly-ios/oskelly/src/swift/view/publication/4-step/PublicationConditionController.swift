//
// Created by Виталий Хлудеев on 27.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationConditionController : SubmitConditionController {

    var draft: Draft!

    override func viewDidLoad() {
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        super.viewDidLoad()
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "OK", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if(draft.selectedCondition == conditions[indexPath.row].id) {
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .top)
        }
        return super.tableView(tableView, cellForRowAt: indexPath)
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        draft.selectedCondition = conditions[indexPath.row].id
        GlobalProvider.instance.draftProvider.setCurrent(draft: draft)
        GlobalProvider.instance.draftProvider.publish(draft: draft) { d in
        }
        navigationController?.popViewController(animated: true)
    }

    func okPressed() {
        navigationController?.popViewController(animated: true)
    }
}