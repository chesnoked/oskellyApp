//
// Created by Виталий Хлудеев on 18.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class SubmitAttributeController : UITableViewController {

    var attribute: Attribute!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(SubmitAttributeRowWithChecker.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        self.navigationItem.title = attribute.name
//        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(PublicationStepsController.saveDraft))
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return attribute.values.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! SubmitAttributeRow
        cell.render(attributeName: attribute.values[indexPath.row].value, selectedValue: nil)
        return cell
    }
}