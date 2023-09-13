package com.enhanced.block;

// IMPORTS
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

// CLASS
public class CustomSlab extends Block implements Waterloggable {

  // PROPERTIES
  public static final DirectionProperty FACING = Properties.FACING;
  public static final BooleanProperty DOUBLE = CustomProperties.DOUBLE;
  public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

  // CONSTRUCTOR
  public CustomSlab(AbstractBlock.Settings settings) {
    super(settings);
    this.setDefaultState(
        (BlockState) ((BlockState) this.getDefaultState().with(Properties.FACING, Direction.NORTH))
            .with(DOUBLE, false)
            .with(WATERLOGGED, false));
  }

  // METHOD
  public static BlockState facing(
      double pos1,
      double pos2,
      DirectionProperty FACING,
      Direction CENTER,
      Direction UP,
      Direction RIGHT,
      Direction DOWN,
      Direction LEFT,
      BlockState STATE) {

    if (pos1 > pos2 && pos1 < -pos2 + 1) {
      return (BlockState) STATE.with(FACING, (pos2 > (0.333f)) ? CENTER : UP);
    } else if (pos1 > pos2 && pos1 > -pos2 + 1) {
      return (BlockState) STATE.with(FACING, (pos1 < (0.666f)) ? CENTER : RIGHT);
    } else if (pos1 < pos2 && pos1 > -pos2 + 1) {
      return (BlockState) STATE.with(FACING, (pos2 < (0.666f)) ? CENTER : DOWN);
    } else if (pos1 < pos2 && pos1 < -pos2 + 1) {
      return (BlockState) STATE.with(FACING, (pos1 > (0.333f)) ? CENTER : LEFT);
    }

    return STATE;

  }

  // METHOD
  @Override
  public boolean hasSidedTransparency(BlockState state) {
    return state.get(DOUBLE) == false;
  }

  // METHOD
  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, DOUBLE, WATERLOGGED);
  }

  // METHOD
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

    Direction direction = state.get(FACING);
    boolean type = state.get(DOUBLE);

    if (type == true)
      return VoxelShapes.fullCube();

    if (direction == Direction.UP)
      return VoxelShapes.cuboid(0.0, 0.5f, 0.0, 1.0f, 1.0f, 1.0f);

    if (direction == Direction.DOWN)
      return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0f, 0.5f, 1.0f);

    if (direction == Direction.NORTH)
      return VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);

    if (direction == Direction.SOUTH)
      return VoxelShapes.cuboid(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);

    if (direction == Direction.WEST)
      return VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);

    if (direction == Direction.EAST)
      return VoxelShapes.cuboid(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);

    return VoxelShapes.fullCube();

  }

  // METHOD
  @Override
  @Nullable
  public BlockState getPlacementState(ItemPlacementContext ctx) {

    BlockPos position = ctx.getBlockPos();
    BlockState state = ctx.getWorld().getBlockState(position);

    if (state.isOf(this)) {
      return (BlockState) ((BlockState) state.with(DOUBLE, true)).with(WATERLOGGED, false);
    }

    FluidState fluid = ctx.getWorld().getFluidState(position);
    BlockState state2 = (BlockState) ((BlockState) this.getDefaultState().with(FACING, Direction.NORTH))
        .with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);

    Direction direction = ctx.getSide();

    double posX = ctx.getHitPos().x - (double) position.getX();
    double posY = ctx.getHitPos().y - (double) position.getY();
    double posZ = ctx.getHitPos().z - (double) position.getZ();

    if (direction == Direction.NORTH) {
      return facing(posX, posY, FACING, Direction.SOUTH, Direction.DOWN, Direction.EAST, Direction.UP,
          Direction.WEST, state2);
    }

    if (direction == Direction.SOUTH) {
      return facing(posX, posY, FACING, Direction.NORTH, Direction.DOWN, Direction.EAST, Direction.UP,
          Direction.WEST, state2);
    }

    if (direction == Direction.WEST) {
      return facing(posZ, posY, FACING, Direction.EAST, Direction.DOWN, Direction.SOUTH, Direction.UP,
          Direction.NORTH, state2);
    }

    if (direction == Direction.EAST) {
      return facing(posZ, posY, FACING, Direction.WEST, Direction.DOWN, Direction.SOUTH, Direction.UP,
          Direction.NORTH, state2);
    }

    if (direction == Direction.UP) {
      return facing(posX, posZ, FACING, Direction.DOWN, Direction.NORTH, Direction.EAST,
          Direction.SOUTH, Direction.WEST, state2);
    }

    if (direction == Direction.DOWN) {
      return facing(posX, posZ, FACING, Direction.UP, Direction.NORTH, Direction.EAST,
          Direction.SOUTH, Direction.WEST, state2);
    }

    return state2;

  }

  // METHOD
  @Override
  public boolean canReplace(BlockState state, ItemPlacementContext context) {

    ItemStack itemStack = context.getStack();
    boolean type = state.get(DOUBLE);
    Direction facing = state.get(FACING);

    if (type == true || !itemStack.isOf(this.asItem())) {
      return false;
    }

    if (context.canReplaceExisting()) {

      Direction direction = context.getSide();

      if (facing == Direction.UP)
        return direction == Direction.DOWN;

      if (facing == Direction.DOWN)
        return direction == Direction.UP;

      if (facing == Direction.NORTH)
        return direction == Direction.SOUTH;

      if (facing == Direction.SOUTH)
        return direction == Direction.NORTH;

      if (facing == Direction.WEST)
        return direction == Direction.EAST;

      if (facing == Direction.EAST)
        return direction == Direction.WEST;

    }

    return true;

  }

  // METHOD
  @Override
  public FluidState getFluidState(BlockState state) {
    if (state.get(WATERLOGGED).booleanValue()) {
      return Fluids.WATER.getStill(false);
    }
    return super.getFluidState(state);
  }

  // METHOD
  @Override
  public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
    if (state.get(DOUBLE) == false) {
      return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }
    return false;
  }

  // METHOD
  @Override
  public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
    if (state.get(DOUBLE) == false) {
      return Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
    }
    return false;
  }

  // METHOD
  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
      WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (state.get(WATERLOGGED).booleanValue()) {
      world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  // METHOD
  @Override
  public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
    switch (type) {
      case LAND: {
        return false;
      }
      case WATER: {
        return world.getFluidState(pos).isIn(FluidTags.WATER);
      }
      case AIR: {
        return false;
      }
    }
    return false;
  }

}
