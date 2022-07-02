//
// Created by Виталий Хлудеев on 13.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitBrandController : UIViewController, UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate {

    var names: [String] = []
    var filteredNames: [String] = []
    let tableView = UITableView()
    let header = UIView()
    let headerHeight: CGFloat = 50.0
    let searchField = SimpleRectangleSearchTextField()
    struct GroupedBrand {
        var sectionName : String!
        var sectionObjects : [Brand]!
    }

    var groupedBrands = [GroupedBrand]()
    var filteredGroupedBrands = [GroupedBrand]()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(header)
        header.snp.makeConstraints{ make in
            make.top.equalTo(view)
            make.left.equalTo(view)
            make.right.equalTo(view)
            make.height.equalTo(50)
        }
        header.addSubview(searchField)
        searchField.snp.makeConstraints({ m in
            m.edges.equalTo(header).inset(UIEdgeInsetsMake(8, 8, 8, 8))
        })
        searchField.textField.addTarget(self, action: #selector(self.onFilterChange), for: .editingChanged)
        searchField.textField.returnKeyType = UIReturnKeyType.done
        searchField.textField.delegate = self

        view.addSubview(tableView)
        tableView.snp.makeConstraints {make in
            make.edges.equalTo(view).inset(UIEdgeInsetsMake(headerHeight, 0, 0, 0))
        }

        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(SubmitAttributeRow.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        self.navigationItem.title = "Выбор бренда"
        GlobalProvider.instance.directoriesProvider.getBrands { result in
            let sortedKeys = Array(result.keys).sorted(by: <)
            self.names = sortedKeys
            self.filteredNames = sortedKeys
            self.groupedBrands = []
            self.filteredGroupedBrands = []
            let sortedBrands = result.sorted(by: { $0.0 < $1.0 })
            for (key, value) in sortedBrands {
                self.groupedBrands.append(GroupedBrand(sectionName: key, sectionObjects: value.sorted(by: { $0.name < $1.name })))
                self.filteredGroupedBrands.append(GroupedBrand(sectionName: key, sectionObjects: value.sorted(by: { $0.name < $1.name })))
            }
            self.tableView.reloadData()
        }
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        return filteredGroupedBrands.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return filteredGroupedBrands[section].sectionObjects.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! SubmitAttributeRow
        cell.render(attributeName: filteredGroupedBrands[indexPath.section].sectionObjects[indexPath.row].name, selectedValue: nil)
        return cell
    }

    public func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return filteredGroupedBrands[section].sectionName
    }

    func sectionIndexTitles(for tableView: UITableView) -> [String]? {
        return self.names
    }

    func onFilterChange() {
        filteredGroupedBrands.removeAll()
        groupedBrands.forEach({ groupedBrand in
            var filteredGroupedBrand = GroupedBrand()
            filteredGroupedBrand.sectionName = groupedBrand.sectionName
            filteredGroupedBrand.sectionObjects = groupedBrand.sectionObjects.filter({ $0.name.lowercased().range(of: (self.searchField.textField.text?.lowercased() ?? "")) != nil ||  (self.searchField.textField.text ?? "") == ""})
            if (!filteredGroupedBrand.sectionObjects.isEmpty) {
                filteredGroupedBrands.append(filteredGroupedBrand)
            }
        })
        tableView.reloadData()
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
}