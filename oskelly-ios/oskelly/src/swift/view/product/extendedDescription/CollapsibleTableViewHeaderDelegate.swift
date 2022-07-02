//
//  CollapsibleTableViewHeaderDelegate.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 02.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

protocol CollapsibleTableViewHeaderDelegate {
    func toggleSection(header: CollapsibleTableViewHeader, section: Int)
}
