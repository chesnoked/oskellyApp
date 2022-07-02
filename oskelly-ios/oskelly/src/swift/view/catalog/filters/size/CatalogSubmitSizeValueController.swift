//
// Created by Виталий Хлудеев on 13.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSubmitSizeValueController : UITableViewController {

    var size: Size!
    var previousController : CatalogSubmitSizeController!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(SubmitAttributeRowWithChecker.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        self.navigationItem.title = size.type
        tableView.allowsMultipleSelection = true
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Очистить", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.clearPressed))
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return size.values.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! SubmitAttributeRow
        cell.render(attributeName: size.values[indexPath.row].name, selectedValue: nil)
        if(previousController.request.size.contains(size.values[indexPath.row].id)) {
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .none)
        }
        return cell
    }

    func okPressed() {
        self.previousController.request.size.removeAll()
        if let indexPaths = tableView.indexPathsForSelectedRows {
            indexPaths.forEach({i in
                self.previousController.request.size.append(size.values[i.row].id)
            })
        }
        self.previousController.tableView.reloadData()
        navigationController?.popViewController(animated: true)
    }

    func clearPressed() {
        previousController.request.size.removeAll()
        tableView.reloadData()
    }
}