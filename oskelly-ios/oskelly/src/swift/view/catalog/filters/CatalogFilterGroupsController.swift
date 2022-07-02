//
// Created by Виталий Хлудеев on 12.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogFilterGroupsController : UITableViewController {

    var previousController : CatalogProductListController!
    var request: ProductRequest!

    override func viewDidLoad() {
        super.viewDidLoad()
        request = previousController.request
        tableView.register(SubmitAttributeRow.self, forCellReuseIdentifier: "Row")
        tableView.separatorStyle = .none
        if let c = previousController.categoryId {
            if (previousController.attributes.isEmpty) {
                GlobalProvider.instance.getApiRequester().getAttributes(categoryId: c) { attributes in
                    self.previousController.attributes = attributes
                    self.tableView.reloadData()
                }
            }
            if (previousController.sizes.isEmpty) {
                GlobalProvider.instance.getApiRequester().getSizes(categoryId: c) { sizes in
                    self.previousController.sizes = sizes
                    self.tableView.reloadData()
                }
            }
        }
        if(previousController.conditions.isEmpty) {
            GlobalProvider.instance.directoriesProvider.getConditions(completionHandler: {conditions in
                self.previousController.conditions = conditions
                self.tableView.reloadData()
            })
        }
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Применить", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.okPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Сбросить", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.cancelPressed))
        navigationItem.hidesBackButton = true
        navigationItem.title = "Фильтры"
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
            case 0:
                return previousController.attributes.count
            case 1:
                return 1
            case 2:
                if(previousController.sizes.isEmpty || previousController.sizes.count == 1) {
                    return 0
                }
                return 1
            case 3:
                if(previousController.conditions.isEmpty) {
                    return 0
                }
                return 1
            case 4:
                return 1 // цена
            default:
                return previousController.attributes.count
        }
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 5
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.section {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
                let map: [Int] = previousController.attributes[indexPath.row].values.map({ $0.id })
                let count = map.filter({ request.filter.contains($0) }).count
                cell.render(attributeName: previousController.attributes[indexPath.row].name, selectedValue: count == 0 ? nil : String(count))
                return cell
            case 1:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
                let count = request.brand.count
                cell.render(attributeName: "Бренд", selectedValue: count == 0 ? nil : String(count))
                return cell
            case 2:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
                let count = request.size.count
                cell.render(attributeName: "Размер", selectedValue: count == 0 ? nil : String(count))
                return cell
            case 3:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
                let count = request.productCondition.count
                cell.render(attributeName: "Состояние", selectedValue: count == 0 ? nil : String(count))
                return cell
            case 4:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
                let startPrice = request.startPrice == nil ? "..." : String(request.startPrice!)
                let endPrice  = request.endPrice == nil ? "..." : String(request.endPrice!)
                cell.render(attributeName: "Цена", selectedValue: request.startPrice == nil && request.endPrice == nil ? nil : startPrice + " - " + endPrice)
                return cell
            default:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! SubmitAttributeRow
                return cell
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44.0
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch indexPath.section {
            case 0:
                let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSubmitAttributeController") as! CatalogSubmitAttributeController
                c.previousController = self
                c.attribute = previousController.attributes[indexPath.row]
                navigationController?.pushViewController(c, animated: true)
            case 1:
                let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSubmitBrandController") as! CatalogSubmitBrandController
                c.previousController = self
                navigationController?.pushViewController(c, animated: true)
            case 2:
                let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSubmitSizeController") as! CatalogSubmitSizeController
                c.request = self.request
                c.sizes = self.previousController.sizes
                c.previousController = self
                navigationController?.pushViewController(c, animated: true)
            case 3:
                let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSubmitConditionController") as! CatalogSubmitConditionController
                c.conditions = self.previousController.conditions
                c.previousController = self
                navigationController?.pushViewController(c, animated: true)
            case 4:
                let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSubmitPriceController") as! CatalogSubmitPriceController
                c.previousController = self
                navigationController?.pushViewController(c, animated: true)
            default:
                print("Error")
        }
    }

    func okPressed() {
        previousController.request = request
        previousController.clearData()
        previousController.loadData()
        navigationController?.popViewController(animated: true)
    }

    func cancelPressed() {
        request.size.removeAll()
        request.brand.removeAll()
        request.filter.removeAll()
        request.productCondition.removeAll()
        request.productState.removeAll()
        request.startPrice = nil
        request.endPrice = nil
        previousController.request = request
        previousController.clearData()
        previousController.loadData()
        navigationController?.popViewController(animated: true)
    }
}