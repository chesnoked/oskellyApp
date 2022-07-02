//
//  ProductSize.swift
//
//  Created by Виталий Хлудеев on 06.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class ProductSize: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let type = "type"
    static let values = "values"
  }

  // MARK: Properties
  public var type: String?
  public var values: [ProductSizeValue]?

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
    type = json[SerializationKeys.type].string
    if let items = json[SerializationKeys.values].array { values = items.map { ProductSizeValue(json: $0) } }
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = type { dictionary[SerializationKeys.type] = value }
    if let value = values { dictionary[SerializationKeys.values] = value.map { $0.dictionaryRepresentation() } }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.type = aDecoder.decodeObject(forKey: SerializationKeys.type) as? String
    self.values = aDecoder.decodeObject(forKey: SerializationKeys.values) as? [ProductSizeValue]
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(type, forKey: SerializationKeys.type)
    aCoder.encode(values, forKey: SerializationKeys.values)
  }

}
