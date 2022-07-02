//
// Created by Виталий Хлудеев on 21.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class DirectoriesProvider {

    private let apiRequester: ApiRequester

    private let defaults = UserDefaults.standard

    init(apiRequester: ApiRequester) {
        self.apiRequester = apiRequester
    }

    func synchronizeBrands() {
        apiRequester.getBrands { brands in
            self.saveBrandsInDefaults(brands: brands)
        }
    }

    func getBrands(completionHandler: @escaping ([String:[Brand]]) -> ()) {
        if(defaults.object(forKey: "brands") == nil) {
            apiRequester.getBrands { brands in
                self.saveBrandsInDefaults(brands: brands)
                completionHandler(brands)
            }
        }
        else {
            let decoded = defaults.object(forKey: "brands")! as! Data
            let brands = NSKeyedUnarchiver.unarchiveObject(with: decoded) as! [String:[Brand]]
            completionHandler(brands)
            apiRequester.getBrands { brands in
                self.saveBrandsInDefaults(brands: brands)
//                completionHandler(brands)
            } // лишним не будет FIXME: !!!!
        }
    }

    private func saveBrandsInDefaults(brands: [String: [Brand]]) {
        self.defaults.set(NSKeyedArchiver.archivedData(withRootObject: brands), forKey: "brands")
    }

    func synchronizeCategories() {
        apiRequester.getCategories(true) { categories in
            self.saveCategoriesInDefaults(true, categories: categories)
        }
        apiRequester.getCategories(false) { categories in
            self.saveCategoriesInDefaults(false, categories: categories)
        }
    }

    func getCategories(_ appendSelf: Bool, completionHandler: @escaping ([ProductCategory]) -> ()) {
        if(defaults.object(forKey: appendSelf ? "appendSelfCategories" : "categories") == nil) {
            apiRequester.getCategories(appendSelf) { categories in
                self.saveCategoriesInDefaults(appendSelf, categories: categories)
                completionHandler(categories)
            }
        }
        else {
            let decoded = defaults.object(forKey: appendSelf ? "appendSelfCategories" : "categories")! as! Data
            let categories = NSKeyedUnarchiver.unarchiveObject(with: decoded) as! [ProductCategory]
            completionHandler(categories)
            apiRequester.getCategories(appendSelf) { categories in
                self.saveCategoriesInDefaults(appendSelf, categories: categories)
//                completionHandler(categories)
            } // лишним не будет FIXME: что-то с этим сделать
        }
    }

    private func saveCategoriesInDefaults(_ appendSelf: Bool, categories: [ProductCategory]) {
        self.defaults.set(NSKeyedArchiver.archivedData(withRootObject: categories), forKey: appendSelf ? "appendSelfCategories" : "categories")
    }

    func getConditions(completionHandler: @escaping ([ProductCondition]) -> ()) {
        if(defaults.object(forKey: "conditions") == nil) {
            apiRequester.getConditions { conditions in
                self.saveConditionsInDefaults(conditions: conditions)
                completionHandler(conditions)
            }
        }
        else {
            let decoded = defaults.object(forKey: "conditions")! as! Data
            let conditions = NSKeyedUnarchiver.unarchiveObject(with: decoded) as! [ProductCondition]
            completionHandler(conditions)
            self.synchronizeConditions() // лишним не будет
        }
    }

    func synchronizeConditions() {
        apiRequester.getConditions { conditions in
            self.saveConditionsInDefaults(conditions: conditions)
        }
    }

    private func saveConditionsInDefaults(conditions: [ProductCondition]) {
        self.defaults.set(NSKeyedArchiver.archivedData(withRootObject: conditions), forKey: "conditions")
    }
}