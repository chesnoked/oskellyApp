//
// Created by Виталий Хлудеев on 21.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit
import DWImagePicker_Swift3

class SecondStepController : UITableViewController, WDImagePickerDelegate {

    var draft: Draft!
    let footer = PublicationStepBaseFooter()

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Фото"
        tableView.register(PublicationPhotoRow.self, forCellReuseIdentifier: "Cell")
        tableView.separatorStyle = .none
        footer.controller = self
        footer.nextStepHandler = self.goToThirdStepController(_:)
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        let camera = DSCameraHandler(delegate_: self)
    }

    func imagePicker(imagePicker: WDImagePicker, pickedImage: UIImage) {

    }

    func imagePickerDidCancel(imagePicker: WDImagePicker) {

    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! PublicationPhotoRow
        cell.render(draft: draft, photoNumber: indexPath.row + 1, controller: self)
        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 94.0
    }

    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return footer
    }
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return PublicationStepBaseFooter.height
    }

    func goToThirdStepController(_ sender: Any){
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "PublicationStepsController") as! PublicationStepsController
        navigationController?.pushViewController(c, animated: true)

    }
}