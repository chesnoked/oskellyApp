//
//  SelectedAttributeValues.swift
//
//  Created by Виталий Хлудеев on 20.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class SelectedAttributeValues: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let id = "id"
    static let attributeId = "attributeId"
    static let name = "name"
    static let value = "value"
  }

  // MARK: Properties
  public var id: Int?
  public var attributeId: Int?
  public var name: String?
  public var value: String?

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  init(
          id: Int,
          value: String,
          attributeId: Int,
          name: String
  ) {
    self.id = id
    self.value = value
    self.attributeId = attributeId
    self.name = name
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    id = json[SerializationKeys.id].int
    attributeId = json[SerializationKeys.attributeId].int
    name = json[SerializationKeys.name].string
    value = json[SerializationKeys.value].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = attributeId { dictionary[SerializationKeys.attributeId] = value }
    if let value = name { dictionary[SerializationKeys.name] = value }
    if let value = value { dictionary[SerializationKeys.value] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.attributeId = aDecoder.decodeObject(forKey: SerializationKeys.attributeId) as? Int
    self.name = aDecoder.decodeObject(forKey: SerializationKeys.name) as? String
    self.value = aDecoder.decodeObject(forKey: SerializationKeys.value) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(attributeId, forKey: SerializationKeys.attributeId)
    aCoder.encode(name, forKey: SerializationKeys.name)
    aCoder.encode(value, forKey: SerializationKeys.value)
  }

}
