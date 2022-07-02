//
// Created by Виталий Хлудеев on 12.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSubmitAttributeController : SubmitAttributeController {

    var previousController : CatalogFilterGroupsController!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.allowsMultipleSelection = true
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Очистить", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.clearPressed))
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = super.tableView(tableView, cellForRowAt: indexPath)
        if(previousController.request.filter.contains(attribute.values[indexPath.row].id)) {
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .none)
        }
        return cell
    }

    func okPressed() {
        attribute.values.forEach({v in
            if let index = self.previousController.request.filter.index(where: {$0 == v.id}) {
                self.previousController.request.filter.remove(at: index)
            }
        })
        if let indexPaths = tableView.indexPathsForSelectedRows {
            indexPaths.forEach({i in
                self.previousController.request.filter.append(attribute.values[i.row].id)
            })
        }
        self.previousController.tableView.reloadData()
        navigationController?.popViewController(animated: true)
    }

    func clearPressed() {
        attribute.values.forEach({v in
            if let index = self.previousController.request.filter.index(where: {$0 == v.id}) {
                self.previousController.request.filter.remove(at: index)
            }
        })
        tableView.reloadData()
    }
}