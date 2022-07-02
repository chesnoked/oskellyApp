//
// Created by Виталий Хлудеев on 13.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSubmitSizeController : UITableViewController {

    var sizes : [Size] = []
    var selectedSize : Size!
    var previousController : CatalogFilterGroupsController!
    var request : ProductRequest!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(SubmitAttributeRow.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        self.navigationItem.title = "Размер"
        if(selectedSize == nil && !sizes.isEmpty) {
            selectedSize = sizes[0]
        }
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Назад", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.backPressed))
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.row {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! SubmitAttributeRow
                cell.render(attributeName: "Тип размера", selectedValue: selectedSize.type)
                return cell
            default:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! SubmitAttributeRow
                let count = request.size.count
                cell.render(attributeName: "Размер", selectedValue: count == 0 ? nil : String(count))
                return cell
        }
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch indexPath.row {
            case 0:
                let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSubmitSizeTypeController") as! CatalogSubmitSizeTypeController
                c.previousController = self
                c.sizes = sizes
                navigationController?.pushViewController(c, animated: true)
            default:
                let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSubmitSizeValueController") as! CatalogSubmitSizeValueController
                c.size = selectedSize
                c.previousController = self
                navigationController?.pushViewController(c, animated: true)
        }
    }

    func okPressed() {
        self.previousController.request = self.request
        self.previousController.tableView.reloadData()
        navigationController?.popViewController(animated: true)
    }

    func backPressed() {
        self.navigationController?.popViewController(animated: true)
    }
}