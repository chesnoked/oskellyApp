//
// Created by Виталий Хлудеев on 14.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitConditionController : UITableViewController {

    var conditions: [ProductCondition] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Состояние"
        tableView.register(SubmitConditionRowWithChecker.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        GlobalProvider.instance.directoriesProvider.getConditions { conditions in
            self.conditions = conditions
            self.tableView.reloadData()
        }
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return conditions.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! SubmitConditionRow
        cell.render(condition: conditions[indexPath.row])
        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 88.0
    }
}