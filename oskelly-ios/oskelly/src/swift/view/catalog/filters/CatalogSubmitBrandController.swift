//
// Created by Виталий Хлудеев on 13.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSubmitBrandController : SubmitBrandController {

    var previousController : CatalogFilterGroupsController!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(SubmitAttributeRowWithChecker.self, forCellReuseIdentifier: "Cell")
        tableView.allowsMultipleSelection = true
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Очистить", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.clearPressed))
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = super.tableView(tableView, cellForRowAt: indexPath)
        let brand = filteredGroupedBrands[indexPath.section].sectionObjects[indexPath.row]
        let brandId = brand.id
        let previousControllerContainsThisBrand = previousController.request.brand.contains(brandId)
        if(previousControllerContainsThisBrand) {
            brand.checked = true
        }
        if(brand.checked) {
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .none)
        }
        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let brandId = filteredGroupedBrands[indexPath.section].sectionObjects[indexPath.row].id
        groupedBrands.forEach({b in
            b.sectionObjects.forEach({ bb in
                if (bb.id == brandId) {
                    bb.checked = true
                }
            })
        })
    }

    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        let brandId = filteredGroupedBrands[indexPath.section].sectionObjects[indexPath.row].id
        groupedBrands.forEach({b in
            b.sectionObjects.forEach({ bb in
                if (bb.id == brandId) {
                    bb.checked = false
                }
            })
        })
    }

    func okPressed() {
        self.previousController.request.brand.removeAll()
        groupedBrands.forEach({b in
            b.sectionObjects.forEach({ bb in
                if (bb.checked) {
                    self.previousController.request.brand.append(bb.id)
                }
            })
        })
        self.previousController.tableView.reloadData()
        navigationController?.popViewController(animated: true)
    }

    func clearPressed() {
        previousController.request.brand.removeAll()
        groupedBrands.forEach({ b in
            b.sectionObjects.forEach({bb in
                bb.checked = false
            })
        })
        onFilterChange()
    }
}