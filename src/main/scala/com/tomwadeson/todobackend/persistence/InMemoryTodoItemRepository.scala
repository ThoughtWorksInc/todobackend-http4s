package com.tomwadeson.todobackend.persistence

import java.util.concurrent.atomic.AtomicLong

import com.tomwadeson.todobackend.domain.{TodoItem, TodoItemForm, TodoItemPartialForm}

import scala.collection.concurrent.TrieMap

class InMemoryTodoItemRepository extends TodoItemRepository {

  private val repository = TrieMap[Long, TodoItem]()
  private val idSequence = new AtomicLong()

  override def getAll: Seq[TodoItem] =
    repository.values.toSeq

  override def getById(id: Long): Option[TodoItem] =
    repository.get(id)

  override def create(todoItemForm: TodoItemForm): TodoItem = {
    val id       = idSequence.getAndIncrement
    val todoItem = TodoItem(id, todoItemForm)
    repository.put(id, todoItem)
    todoItem
  }

  override def delete(id: Long): Unit =
    repository.remove(id)

  override def deleteAll: Unit =
    synchronized {
      idSequence.set(0)
      repository.clear()
    }

  override def update(id: Long, todoItemForm: TodoItemPartialForm): Option[TodoItem] = {
    val item = repository.get(id).map(_.update(todoItemForm))
    item.foreach(repository.update(id, _))
    item
  }
}
