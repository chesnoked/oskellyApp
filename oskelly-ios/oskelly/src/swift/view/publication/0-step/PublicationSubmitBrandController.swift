//
// Created by Виталий Хлудеев on 16.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationSubmitBrandController : SubmitBrandController{

    var selectedCategoryId: Int = -1

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)

        GlobalProvider.instance.getApiRequester().firstStep(categoryId: selectedCategoryId, brandId: filteredGroupedBrands[indexPath.section].sectionObjects[indexPath.row].id) { d in
            let draft = GlobalProvider.instance.draftProvider.setCurrent(draft: d)
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let c = storyboard.instantiateViewController(withIdentifier: "PublicationStepsController") as! PublicationStepsController
            self.navigationController?.pushViewController(c, animated: true)
        }
    }
}