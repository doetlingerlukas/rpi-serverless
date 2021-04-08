class Handler
  def run(req)
    return req.delete(' ').split(',').map(&:to_i).reduce(0, :+)
  end
end