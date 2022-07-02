//
//  SaleGroup.swift
//
//  Created by Виталий Хлудеев on 17.12.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class SaleGroup: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let groupName = "groupName"
    static let sales = "sales"
  }

  // MARK: Properties
  public var groupName: String!
  public var sales: [Sale]! = []

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    groupName = json[SerializationKeys.groupName].string
    if let items = json[SerializationKeys.sales].array { sales = items.map { Sale(json: $0) } }
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = groupName { dictionary[SerializationKeys.groupName] = value }
    if let value = sales { dictionary[SerializationKeys.sales] = value.map { $0.dictionaryRepresentation() } }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.groupName = aDecoder.decodeObject(forKey: SerializationKeys.groupName) as? String
    self.sales = aDecoder.decodeObject(forKey: SerializationKeys.sales) as? [Sale]
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(groupName, forKey: SerializationKeys.groupName)
    aCoder.encode(sales, forKey: SerializationKeys.sales)
  }

}
