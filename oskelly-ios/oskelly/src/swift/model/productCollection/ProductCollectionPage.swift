//
//  ProductCollectionPage.swift
//
//  Created by Виталий Хлудеев on 13.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class ProductCollectionPage: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let items = "items"
    static let totalPagesCount = "totalPagesCount"
    static let totalItemsCount = "totalItemsCount"
  }

  // MARK: Properties
  public var items: [ProductCollectionItem]?
  public var totalPagesCount: Int?
  public var totalItemsCount: Int?

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
    if let i = json[SerializationKeys.items].array { items = i.map { ProductCollectionItem(json: $0) } }
    if let value = json[SerializationKeys.totalPagesCount].int { totalPagesCount = value } else {totalPagesCount = 1}
    totalItemsCount = json[SerializationKeys.totalItemsCount].int
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = items { dictionary[SerializationKeys.items] = value.map { $0.dictionaryRepresentation() } }
    if let value = totalPagesCount { dictionary[SerializationKeys.totalPagesCount] = value }
    if let value = totalItemsCount { dictionary[SerializationKeys.totalItemsCount] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.items = aDecoder.decodeObject(forKey: SerializationKeys.items) as? [ProductCollectionItem]
    self.totalPagesCount = aDecoder.decodeObject(forKey: SerializationKeys.totalPagesCount) as? Int
    self.totalItemsCount = aDecoder.decodeObject(forKey: SerializationKeys.totalItemsCount) as? Int
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(items, forKey: SerializationKeys.items)
    aCoder.encode(totalPagesCount, forKey: SerializationKeys.totalPagesCount)
    aCoder.encode(totalItemsCount, forKey: SerializationKeys.totalItemsCount)
  }

}
